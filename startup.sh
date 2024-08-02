docker network create book-net
docker run -d --name mysql --network book-net --network-alias mysql -v mysql:/var/lib/mysql -e MYSQL_RANDOM_ROOT_PASSWORD=yes -e MYSQL_DATABASE=books -e MYSQL_USER=book -e MYSQL_PASSWORD=book!23$ mysql:latest
sleep 40
docker run -d --name book --network book-net -p 8080:8080 -e DB_HOST=mysql -e DB_USER=book -e DB_PASSWORD=book!23$ -e DB_NAME=books book:0.1