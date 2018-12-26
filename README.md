Project:
:graal -- native image generation added through plugin

File path to proxy file must be changed here:
-> option("-H:DynamicProxyConfigurationFiles=/home/sergej/IdeaProjects/prototyping/graal/proxies.json")

Task:

Generate native-image
./gradlew :graal:nativeImage