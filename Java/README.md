#MyLittleCheapLibrary

Architecture **Service Provider Framework**.

Services proposés :

* Chiffrement 
* Authentification
* Contrôle d'intégrité

##Provider Bouncy Castle

Il est impératif d'ajouter le provider **Bouncy Castle** dans le fichier `java.security` afin de pouvoir utiliser les services se basant sur ce provider:

```
[...]
security.provider.<number>=org.bouncycastle.jce.provider.BouncyCastleProvider
[...]
```

##Fichier de configuration

La librairie utilise le fichier de configuration `db_access.properties`. Ce fichier de configuration doit être placé dans le répertoire utilisateur (peut être caché). Il s'agit d'un fichier *properties* du monde Java.

Liste des propriétés possibles :

```
DRIVER   = com.mysql.jdbc.Driver
USERNAME = <username>
PASSWORD = <password>
HOST     = <ip_or_hostname>
PORT     = <port>
DBNAME   = <data_base_name>
```

##Jars

Tous les __jars__ nécessaires au bon fonctionnement de cette librairie sont placés dans le répertoire `jars/`.

##Copyright

Institut Supérieur Industriel Liégeois (ISIL) - Département ingénieurs industriels

[Mawet Xavier](http://www.nakim.be) © 2014-2015. All rights reserved.