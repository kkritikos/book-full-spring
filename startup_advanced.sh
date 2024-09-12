docker network create book-net
docker run -d --name mysql --network book-net --network-alias mysql -v mysql:/var/lib/mysql -e MYSQL_RANDOM_ROOT_PASSWORD=yes -e MYSQL_DATABASE=books -e MYSQL_USER=book -e MYSQL_PASSWORD=book1234 mysql:8.3.0

while ! docker exec -it mysql mysql -ubook -pbook1234 -e "SELECT 1" >/dev/null 2>&1; do
    sleep 1
done

docker run -d --name book --network book-net -p 8080:8080 -e DB_HOST=mysql -e DB_USER=book -e DB_PASSWORD=book1234 -e DB_NAME=books book:0.1