scp -i /h/.ssh/id_rcs-operator \
	/c/devsbb/tmp/tibco/FTL\ -\ Enterprise\ Edition/6.1.0/TIB_ftl_6.1.0_linux_x86_64.zip \
	cloud-user@shared-rcsactivemq-node03.otc-test.sbb.ch:/tmp/

scp -i /h/.ssh/id_rcs-operator \
	/c/devsbb/tmp/tibco/eFTL\ -\ Enterprise\ Edition/6.1.0/TIB_eftl_6.1.0_linux_x86_64.zip \
	cloud-user@shared-rcsactivemq-node03.otc-test.sbb.ch:/tmp/

ssh cloud-user@shared-rcsactivemq-node03.otc-test.sbb.ch -i /h/.ssh/id_rcs-operator

sudo su
cd /tmp
unzip TIB_ftl_6.1.0_linux_x86_64.zip
unzip TIB_eftl_6.1.0_linux_x86_64.zip
yum install -y TIB_*/rpm/*rpm
yum install wget glances

mkdir /opt/tibco/ftl/sbb/
wget -O /opt/tibco/ftl/sbb/hh_test.yaml \
	https://code.sbb.ch/users/ue85540/repos/ftl_playground/raw/sbb_test.yml?at=refs%2Fheads%2Fmaster
wget -O /etc/systemd/system/tibftl.service \
	https://code.sbb.ch/users/ue85540/repos/ftl_playground/raw/tibftl.service?at=refs%2Fheads%2Fmaster

systemctl start tibftl.service