# BisectHostingStats Folia
Welcome to GitHub page for the BisectHostingStats plugin.

## Features
- Configurable options to measure the TPS of the laggiest region.
- Display the regular information like entity counts or chunks loaded. 
- The CPU, and Ram is always going to be for the general server and not per world (or region). 
- You can also set it to see the tps of a certain world as well. 

(This does not have the option to see general or per-world thread usage. This would likely have to be added on the website backend).

## Dependencies
Spark, Folia

## Description

This is a versatile plugin where:

- You can configure to see the tps, entity count and loaded chunks per world.
- You can set the config.yml to see the laggiest regions tps, entity count, and loaded chunks for the general server or the world of your choosing
- You can turn off tracking statistics of TPS, entity count, or loaded chunks on the server.

This is a ultra light-weight plugin which should barely register on any timings or spark report.

## Commands
`/bisecthostingstats reload` - Reloads the config.yml settings

## Permissions
`bisecthostingfolia.command.bisecthostingstats` - Allows the user to use the `/bisecthostingstats` command

## Config
Example config.yml
```yaml
stats:
  tps:
    enabled: true
    only-show-stats-for-world: ""
  entities:
    enabled: true
    only-show-stats-for-world: ""
  chunks:
    enabled: true
    only-show-stats-for-world: ""
```

## Sponsors
This plugin was sponsored by yomameatstoes#6491 on discord, or known as nucklear on the Havok minecraft server