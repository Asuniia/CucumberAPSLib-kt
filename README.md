# CucumberAPSLib-KT

Asuniia/CucumberAPSLib-kt contient une librairie conçus pour la JVM. Elle permet d'utiliser facilement le systeme de license de CucumberAPS.

## Requis

* Java 8 au minimum

## Exemple

Voici un exemple d'integration avec Spigot. Dans cette exemple la clé de license sera reverifier toutes les 15 minutes. En cas d'échec le programme plantera, sinon elle loggera un message.

```java
package com.exemple.license_demo;

import java.lang.*;
import org.bukkit.plugin.java.JavaPlugin;
import com.cucumber.aps.AuthLicense;
import com.cucumber.aps.AuthMode;

class Main : JavaPlugin() {

    override fun onEnable() {
        AuthLicense("7bf2f6ca-1632-495b-8d86-5458f316b6bd")
            .mode(AuthMode::Classic)
            .onSuccess { logger.info("License of $name could be correctly verified") }
            .onFailure { System.exit() }
            .each(15 * 60 * 1000)
            .verify()
    }
}
```

## Documentation

Cette librairie fournis une seul class qui va prendre dans son contructeur un clé de license. Afin de verifier cette clé il suffit d'appeler la method verify. Vous pouvez planifier spécifier un delay avec la method ``each`` qui va relancer automatiquement la verification. Vous pouvez passer une fonction en cas de reussite avec la method ``onSuccess`` et une fonction en cas d'échec avec ``onFailure``.