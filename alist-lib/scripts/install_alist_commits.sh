SHA=$(curl -s https://api.github.com/repos/alist-org/alist/commits/main | grep -o '"sha": ".*"' | cut -d'"' -f4 | head -n 1)

echo "Current latest sha: https://github.com/alist-org/alist/tree/${SHA}"

curl -L -o "main.zip" https://github.com/alist-org/alist/archive/refs/heads/main.zip
unzip -o main.zip
mv -f alist-main/* ../

echo "Write sha to local.properties"
cd ../../
touch local.properties
sed -i '/ALIST_COMMIT_SHA/d' local.properties
echo "ALIST_COMMIT_SHA=${SHA}" >> local.properties
