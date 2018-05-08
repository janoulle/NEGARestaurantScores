# Docker build instructions
1. To build, tag, and push, run ```gradlew buildAndTagImage --console=plain --stacktrace -PdockerImageVersion=YOUR_VERSION -PpathToRepos=/path/to/repos.json```. The `pathToRepos` property should be .json file containing a map of key:value pairs where the value is the remote repository name
2. To just build the image, run ```gradlew docker --console=plain --stacktrace -PdockerImageVersion=YOUR_VERSION```
3. Note: the --console and --stacktrace flags are for troubleshooting purposes


# Docker run instructions

1. This application relies on environment variables for the app to start. Review Requirements.txt as a sample .env file and provide the needed values. The key names are hopefully clear enough to understand the values to be provided.
2. Review docker-compose.yml and provide the paths to the .env files specified.
3. In the docker-compose.yml file, update the restaurantscores-server.image setting to point to your local or remote image e.g. `restaurantscores-server:YOUR_VERSION`.
4. At the root of the project, open a Terminal tab and type `cd docker`. Then, type `docker-compose up` to start the containers.