FROM centos:centos7
ENV VERSION=1.2.0
RUN yum install -y zip && yum install -y unzip
RUN yum install -y python3
COPY resources/com/ci-task/python-zip-pipeline/zip_job.py /tmp
CMD cat /etc/redhat-release ; test -f /tmp/zip_job.py && echo "zip_job.py exists." || echo "zip_job.py does not exist." ; cat