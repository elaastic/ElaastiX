#!/bin/sh

if ! command -v docker >/dev/null 2>&1
then
    echo "ERROR: Docker is not installed on your system!"
    echo "Please install Docker and run \`mise install\` again."
    exit 1
fi

echo "Preparing Garage..."

GARAGE_STOP=0
GARAGE_CONTAINER=$(docker ps -aq -f 'status=running' -f 'name=elaastix-storage')
if [ -z "$GARAGE_CONTAINER" ]; then

    GARAGE_STOP=1
    GARAGE_CONTAINER=$(docker compose run --rm -d storage /garage server 2> /dev/null)
fi

garage() {
    docker exec --env RUST_LOG=garage=warn $GARAGE_CONTAINER /garage "$@"
}

# Derived from https://git.deuxfleurs.fr/Deuxfleurs/garage/src/commit/60eee993b4e424d3e83fdd5d008a19bb04d5bcbd/script/dev-configure.sh
garage status \
    | grep 'NO ROLE' \
    | grep -Po '^[0-9a-f]+' \
    | while read id; do
          garage layout assign $id -z dc1 -c 10G
      done

garage layout apply --version 1 > /dev/null 2>&1 # Let it fail, doing this the "clean" way is annoying

# Derived from https://git.deuxfleurs.fr/Deuxfleurs/garage/src/commit/60eee993b4e424d3e83fdd5d008a19bb04d5bcbd/script/dev-bucket.sh
KEY_INFO=$(garage json-api GetKeyInfo '{"showSecretKey":true,"search":"Development Root Key"}' 2> /dev/null)
if [ -z "$KEY_INFO" ]; then
    KEY_INFO=$(garage json-api CreateKey '{"name":"Development Root Key"}')
fi

ACCESS_KEY=$(echo $KEY_INFO | jq -r .accessKeyId)
SECRET_KEY=$(echo $KEY_INFO | jq -r .secretAccessKey)

if [ ! -f .config/garage/credentials.env ]; then
    echo "ACCESS_KEY=$ACCESS_KEY" >  .config/garage/credentials.env
    echo "SECRET_KEY=$SECRET_KEY" >> .config/garage/credentials.env
fi

create_bucket() {
    if garage bucket info $1 > /dev/null 2>&1; then true; else
        garage bucket create $1
        garage bucket allow $1 --read --write --owner --key $ACCESS_KEY
    fi
}

create_bucket elaastix-attachments

if [ $GARAGE_STOP -ne 0 ]; then
    docker stop "$GARAGE_CONTAINER" > /dev/null
fi

echo "Configured Garage."
