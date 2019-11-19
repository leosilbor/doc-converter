FROM ubuntu:18.04

RUN apt-get update && apt-get upgrade -y && apt-get -y install wget 

RUN wget --no-check-certificate http://download.documentfoundation.org/libreoffice/stable/6.2.7/deb/x86_64/LibreOffice_6.2.7_Linux_x86-64_deb.tar.gz \
        && tar -xvf LibreOffice_6.2.7_Linux_x86-64_deb.tar.gz \
        && cd LibreOffice_6.2.7.1_Linux_x86-64_deb/DEBS && dpkg -i *.deb \
        && cd .. && cd .. && rm -f LibreOffice_6.2.7_Linux_x86-64_deb.tar.gz \
        && rm -rf LibreOffice_6.2.7.1_Linux_x86-64_deb

RUN apt-get -y install openjdk-8-jdk

VOLUME /tmp

COPY target/*.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Doffice.home=/opt/libreoffice6.2","-jar","/app.jar"]