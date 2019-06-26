<img alt="Banner" src="assets/img/banner.png" style="border-radius:10px">

<h1 align="center">DisPay</h1>
<p align="center"><i>Made with :heart: by <a href="https://github.com/GreatGodApollo">@GreatGodApollo</a> and <a href="https://github.com/averen">@averen</a></i></p>

Discord Hack Week Submission of 2019

Check out our [website](https://dispay.xyz)!

## Submission Category

Productivity

## Built With

- [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA)
- [Spark Framework](https://github.com/perwendel/spark)
- [Logback Classic](https://logback.qos.ch)
- [JSON in Java](https://github.com/stleary/JSON-java)
- [Reflections](https://github.com/ronmamo/reflections)
- [Jedis](https://github.com/xetorthio/jedis)

## Licensing

This project is licensed under the [GNU General Public License v3.0](https://choosealicense.com/licenses/gpl-3.0/)

## Configuration

DisPay is configurable by using a `config.json` file. Example:
```json
{
    "token": "your bot token or redis",
    "port": 7777,
    "redis": {
        "host": "localhost",
        "database": 1
    }
}
```
All fields except `token` are optional. You may also specify "redis" as your token and DisPay will look for the token on your redis database under the key `token`.

## Team Members

- apollo#9292
- avery#1235
