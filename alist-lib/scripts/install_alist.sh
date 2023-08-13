TAG_NAME=$(curl -s https://api.github.com/repos/alist-org/alist/releases/latest | grep -o '"tag_name": ".*"' | cut -d'"' -f4)

URL="https://github.com/alist-org/alist/archive/refs/tags/${TAG_NAME}.tar.gz"
echo "Downloading alist ${TAG_NAME} from ${URL}"

curl -L -o "alist.tgz" $URL
tar xf "alist.tgz" --strip-components 1 -C ../
