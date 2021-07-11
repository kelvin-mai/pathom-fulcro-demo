# build environment
FROM clojure:tools-deps AS builder
ADD . /src
WORKDIR /src
COPY ./src/ /src
RUN curl -sL https://deb.nodesource.com/setup_14.x | bash -
RUN apt-get install -y nodejs 
RUN npm install
RUN NODE_ENV="production" npm run css
RUN npx shadow-cljs release :main
RUN clj -X:build

# create small runtime image debian11 + openjdk11 + compilation output
FROM debian:bullseye-20210511-slim
RUN apt-get -qq update
RUN mkdir -p /usr/share/man/man1	# bug with -slim + openjdk11
RUN apt-get -qq --no-install-recommends -y install openjdk-11-jre-headless
WORKDIR /opt/app
COPY --from=builder /src/app.jar ./app.jar
CMD ["java","-jar","app.jar"] 
