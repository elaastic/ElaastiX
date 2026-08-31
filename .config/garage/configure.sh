#!/bin/sh
echo "GARAGE_DEFAULT_ACCESS_KEY=GK$(openssl rand -hex 16)" > .config/garage/credentials.env
echo "GARAGE_DEFAULT_SECRET_KEY=$(openssl rand -hex 32)"  >> .config/garage/credentials.env
