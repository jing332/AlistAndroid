VERSION=3.25.1

curl -L -o "alist.tgz" "https://github.com/alist-org/alist/archive/refs/tags/v${VERSION}.tar.gz"
tar xf "alist.tgz" --strip-components 1 -C ./