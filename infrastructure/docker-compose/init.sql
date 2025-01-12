-- grant all privileges to brunosong for all hosts
GRANT ALL PRIVILEGES ON *.* TO 'brunosong'@'%' IDENTIFIED BY '1234' WITH GRANT OPTION;
FLUSH PRIVILEGES;
