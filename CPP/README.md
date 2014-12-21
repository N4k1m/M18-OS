#Client Documents
###Fichier de configuration
L'application **ClientDocuments** utilise le fichier de configuration `client_documents.conf`. Ce fichier de configuration doit être placé dans le **même répertoire que l'exécutable**. La syntaxe est semblable à celle des fichiers *properties* du monde Java.

Liste des propriétés possibles :
```
default_port=<port>
default_ip=<ip>
default_username=<username>
default_password=<password>
commandDelimiter=<char>
headerDelimiter=<char>
endDelimiter=<char>
```

#Server Documents

###Fichier de configuration
L'application **ServerDocuments** utilise le fichier de configuration `server_documents.conf`. Ce fichier de configuration doit être placé dans le **même répertoire que l'exécutable**. La syntaxe est semblable à celle des fichiers *properties* du monde Java.

Liste des propriétés possibles :
```
port=<port>
commandDelimiter=<char>
headerDelimiter=<char>
endDelimiter=<char>
```

###Fichier des utilisateurs (DB)
La base de données est simulée par un simple fichier texte `users.conf`, dans lequel les données des utilisateurs sont écrites ligne par ligne :
```
<username>=<password>
```
Ce fichier doit être placé dans un répertoire `DB/` qui, lui même, doit être placé dans le **même répertoire que l'exécutable**.

###Fichiers (non) chiffrés récupérables par les clients
Le **ClientDocuments** peut récupérer des fichiers texte non chiffrés ou chiffrés. Ces derniers sont placés respectivement dans les répertoires `PLAIN/` et `CIPHER/` qui, eux même, doivent être placés dans le **même répertoire que l'exécutable**. 
