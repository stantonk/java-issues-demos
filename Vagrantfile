# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|

    config.vm.box = "ubuntu/trusty64"

    config.vm.network "private_network", ip: "192.168.5.2"
    # config.vm.network :bridged

    config.vm.network "forwarded_port", guest: 8080, host: 8080
    # JMX
    config.vm.network "forwarded_port", guest: 9000, host: 9000

    config.vm.provision "shell", inline: <<-SHELL
        sudo apt-get update && sudo apt-get upgrade

        # oracle jdk
        sudo mkdir /opt/jdk
        sudo cp /vagrant/jdk-8u161-linux-x64.tar.gz /tmp/
        sudo tar -zxvf /tmp/jdk-8u161-linux-x64.tar.gz -C /opt/jdk
        sudo update-alternatives --install /usr/bin/java java /opt/jdk/jdk1.8.0_161/bin/java 100
        sudo update-alternatives --install /usr/bin/javac javac /opt/jdk/jdk1.8.0_161/bin/javac 100
        sudo java -version
        sudo rm /tmp/jdk-8u161-linux-x64.tar.gz

        # docker
        sudo apt-get install -y    linux-image-extra-$(uname -r)     linux-image-extra-virtual
        sudo apt-get install -y linux-headers-3.13.0-125-generic
        sudo apt-get update
        sudo apt-get install -y    apt-transport-https     ca-certificates     curl     software-properties-common
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
        sudo apt-key fingerprint 0EBFCD88
        sudo add-apt-repository    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
           $(lsb_release -cs) \
           stable"
        sudo apt-get update
        sudo apt-get install -y docker-ce

        sudo docker run hello-world
        sudo usermod -aG docker vagrant

        # maven
        wget http://mirrors.koehn.com/apache/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz
        tar xzvf apache-maven-3.5.2-bin.tar.gz
        echo "export PATH=$HOME/apache-maven-3.5.2/bin:\$PATH" >> $HOME/.bashrc
        source $HOME/.bashrc
        mvn -v
    SHELL


end
