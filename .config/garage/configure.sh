#!/bin/sh

if ! command -v docker >/dev/null 2>&1
then
    echo "ERROR: Docker is not installed on your system!"
    echo "Please install Docker and run \`mise install\` again."
    exit 1
fi

GARAGE_PID=0
GARAGE_CONTAINER=$(docker ps -aq -f 'status=running' -f 'name=elaastix-storage')
if [ -z "$GARAGE_CONTAINER" ]; then
    echo "Starting Garage server"
    docker compose run --rm storage /garage server > /dev/null 2>&1 &

    GARAGE_PID=$!
    while [ -z "$GARAGE_CONTAINER" ]; do
        sleep 1
        GARAGE_CONTAINER=$(docker ps -aq -f 'status=running' -f 'name=elaastix-storage')
    done
else
    echo "Using currently running Garage server"
fi

garage() {
    docker exec --env RUST_LOG=garage=warn $GARAGE_CONTAINER /garage "$@"
}

# Derived from https://git.deuxfleurs.fr/Deuxfleurs/garage/src/commit/60eee993b4e424d3e83fdd5d008a19bb04d5bcbd/script/dev-configure.sh
garage status \
    | grep 'NO ROLE' \
    | grep -Po '^[0-9a-f]+' \
    | while read id; do
          echo "Assigning layout to node $id"
          garage layout assign $id -z dc1 -c 10G
      done

garage layout apply --version 1 > /dev/null 2>&1 # Let it fail, doing this the "clean" way is annoying

# Derived from https://git.deuxfleurs.fr/Deuxfleurs/garage/src/commit/60eee993b4e424d3e83fdd5d008a19bb04d5bcbd/script/dev-bucket.sh
KEY_INFO=$(garage json-api GetKeyInfo '{"search":"Development Root Key"}' 2> /dev/null)
if [ -z "$KEY_INFO" ]; then
    echo "Creating access key and secret pair"
    KEY_INFO=$(garage json-api CreateKey '{"name":"Development Root Key"}')
    ACCESS_KEY=$(echo $KEY_INFO | jq -r .accessKeyId)
    SECRET_KEY=$(echo $KEY_INFO | jq -r .secretAccessKey)

    echo "ACCESS_KEY=$ACCESS_KEY" >  .config/garage/credentials.env
    echo "SECRET_KEY=$SECRET_KEY" >> .config/garage/credentials.env
else
    ACCESS_KEY=`echo $KEY_INFO | jq -r .accessKeyId`
fi

create_bucket() {
    if garage bucket info $1 > /dev/null 2>&1; then true; else
        echo "Creating bucket '$1'"
        garage bucket create $1
        garage bucket allow $1 --read --write --owner --key $ACCESS_KEY
    fi
}

create_bucket elaastix-attachments

if [ $GARAGE_PID -ne 0 ]; then
    echo "Shutting down Garage"
    kill $GARAGE_PID
fi

echo "Done. Garage is ready for use."
