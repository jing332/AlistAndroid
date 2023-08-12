package alistlib

import (
	"context"
	"fmt"
	"github.com/alist-org/alist/v3/cmd"
	"github.com/alist-org/alist/v3/cmd/flags"
	"github.com/alist-org/alist/v3/internal/bootstrap"
	"github.com/alist-org/alist/v3/internal/conf"
	"github.com/alist-org/alist/v3/pkg/utils"
	"github.com/alist-org/alist/v3/server"
	"github.com/gin-gonic/gin"
	log "github.com/sirupsen/logrus"
	"net"
	"net/http"
	"os"
	"strconv"
	"sync"
	"time"
)

type MyHook struct {
	log.Hook

	cb LogCallback
}

func (h MyHook) Fire(entry *log.Entry) error {
	h.cb.onLog(int16(entry.Level), entry.Message)

	return nil
}

type LogCallback interface {
	onLog(level int16, msg string)
}

func Init(cb LogCallback) {
	utils.Log.Hooks.Add(MyHook{
		cb: cb,
	})
}

var httpSrv, httpsSrv, unixSrv *http.Server

// Start starts the server
func Start() error {
	cmd.Init()
	if conf.Conf.DelayedStart != 0 {
		utils.Log.Infof("delayed start for %d seconds", conf.Conf.DelayedStart)
		time.Sleep(time.Duration(conf.Conf.DelayedStart) * time.Second)
	}
	bootstrap.InitAria2()
	bootstrap.InitQbittorrent()
	bootstrap.LoadStorages()
	if !flags.Debug && !flags.Dev {
		gin.SetMode(gin.ReleaseMode)
	}
	r := gin.New()
	r.Use(gin.LoggerWithWriter(log.StandardLogger().Out), gin.RecoveryWithWriter(log.StandardLogger().Out))
	server.Init(r)

	if conf.Conf.Scheme.HttpPort != -1 {
		httpBase := fmt.Sprintf("%s:%d", conf.Conf.Scheme.Address, conf.Conf.Scheme.HttpPort)
		utils.Log.Infof("start HTTP server @ %s", httpBase)
		httpSrv = &http.Server{Addr: httpBase, Handler: r}
		go func() {
			err := httpSrv.ListenAndServe()
			if err != nil && err != http.ErrServerClosed {
				utils.Log.Fatalf("failed to start http: %s", err.Error())
			}
		}()
	}
	if conf.Conf.Scheme.HttpsPort != -1 {
		httpsBase := fmt.Sprintf("%s:%d", conf.Conf.Scheme.Address, conf.Conf.Scheme.HttpsPort)
		utils.Log.Infof("start HTTPS server @ %s", httpsBase)
		httpsSrv = &http.Server{Addr: httpsBase, Handler: r}
		go func() {
			err := httpsSrv.ListenAndServeTLS(conf.Conf.Scheme.CertFile, conf.Conf.Scheme.KeyFile)
			if err != nil && err != http.ErrServerClosed {
				utils.Log.Fatalf("failed to start https: %s", err.Error())
			}
		}()
	}
	if conf.Conf.Scheme.UnixFile != "" {
		utils.Log.Infof("start unix server @ %s", conf.Conf.Scheme.UnixFile)
		unixSrv = &http.Server{Handler: r}
		go func() {
			listener, err := net.Listen("unix", conf.Conf.Scheme.UnixFile)
			if err != nil {
				utils.Log.Fatalf("failed to listen unix: %+v", err)
			}
			// set socket file permission
			mode, err := strconv.ParseUint(conf.Conf.Scheme.UnixFilePerm, 8, 32)
			if err != nil {
				utils.Log.Errorf("failed to parse socket file permission: %+v", err)
			} else {
				err = os.Chmod(conf.Conf.Scheme.UnixFile, os.FileMode(mode))
				if err != nil {
					utils.Log.Errorf("failed to chmod socket file: %+v", err)
				}
			}
			err = unixSrv.Serve(listener)
			if err != nil && err != http.ErrServerClosed {
				utils.Log.Fatalf("failed to start unix: %s", err.Error())
			}
		}()
	}

	utils.Log.Println("Server exit")

	return nil
}

// Shutdown timeout毫秒
func Shutdown(timeout int64) error {
	var errChan = make(chan error)

	timeoutDuration := time.Duration(timeout) * time.Millisecond
	utils.Log.Println("Shutdown server...")
	ctx, cancel := context.WithTimeout(context.Background(), timeoutDuration)
	defer cancel()
	var wg sync.WaitGroup
	if conf.Conf.Scheme.HttpPort != -1 {
		wg.Add(1)
		go func() {
			defer wg.Done()
			errChan <- httpSrv.Shutdown(ctx)
		}()
	}
	if conf.Conf.Scheme.HttpsPort != -1 {
		wg.Add(1)
		go func() {
			defer wg.Done()
			errChan <- httpsSrv.Shutdown(ctx)
		}()
	}
	if conf.Conf.Scheme.UnixFile != "" {
		wg.Add(1)
		go func() {
			defer wg.Done()
			errChan <- unixSrv.Shutdown(ctx)
		}()
	}
	wg.Wait()

	select {
	case err := <-errChan:
		return err
	}
}
