[![Build Status](https://api.travis-ci.org/unige-pinfo-2018/PInfo1-dev.svg?branch=master)](https://api.travis-ci.org/unige-pinfo-2018/PInfo1-dev.svg?branch=master)

# unihub-dev

## Build Setup

```
# please clean your docker-related stuff (containers, images, network):
https://gist.github.com/bastman/5b57ddb3c11942094f8d0a97d461b430

sudo mkdir /opt/docker-deploy
sudo chmod -R 755 /opt/docker-deploy
sudo chmod -R a+x /opt/docker-deploy
sudo chown -R user:user /opt/docker-deploy

cd docker-config
./runDocker
cd ..
mvn clean install
```
