# Gamocord
A simple discord bot to handle gamocosm servers


### Compiling
As any other maven project, run `mvn clean install` and the output jar will be in the `target` folder. The bot requires java 11 or above.


### Running
Make sure you have the sample [config file](https://github.com/Pablete1234/Gamocord/blob/master/src/main/resources/config.properties) on the same folder and edit it with accordingly.
Then as any other java program, run with `java -jar Gamocord*.jar`

### Running with Docker
Download the sample [config file](https://github.com/Pablete1234/Gamocord/blob/master/src/main/resources/config.properties) in a directory then do:
```
docker run --name=gamocord -dit --restart=unless-stopped -v $(pwd)/config.properties:/gamocord/config.properties quay.io/unixfox/gamocord:latest
```