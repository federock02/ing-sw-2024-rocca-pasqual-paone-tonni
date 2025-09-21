# Software Engineering Project A.A. 2023-2024

<img src="src/main/resources/images/Background_image/background_complete.jpg" width=500 px align="right" >

_Codex_ _Naturalis_ is the final test of **"Software Engineering"** course of **"Computer Science Engineering"** held at Politecnico di Milano (2023/2024).

**Professor**: Gianpaolo Saverio Cugola

**Team**:

Federico Rocca, 
Matteo Pasqual, 
Arianna Paone, 
Alberto Tonni.

**Group**: GC32

**Score**: 30L/30


## UML
UML files and Communication Protocol.

- [Low Level UML](https://github.com/federock02/ing-sw-2024-rocca-pasqual-paone-tonni/blob/master/Derivables/UML/UML_basso_livello_final.pdf): the low level UML with all fields and methods; 
- [High level UML](https://github.com/federock02/ing-sw-2024-rocca-pasqual-paone-tonni/blob/master/Derivables/UML/UML_alto_livello_final.pdf): high level UML with only project's skeleton;
- [Communication Protocol](https://github.com/federock02/ing-sw-2024-rocca-pasqual-paone-tonni/blob/master/Derivables/UML/protocollo_di_rete.pdf): the communication protocol TCP and RMI.


## Implemented features
| Functionality      |           Status            |
|:-------------------|:---------------------------:|
| Basic rules        |            [✅]()          |
| Complete rules     |            [✅]()          |
| Socket (TCP)       |            [✅]()          |
| RMI                |            [✅]()          |
| TUI                |            [✅]()          |
| GUI                |            [✅]()          | 
| [FA]: Multiple games     |            [✅]()          |
| [FA]: Chat               |            [✅]()          |
| [FA]: Connection Resilience    |            [✅]()          |
| [FA]: Persistence        |             ⛔             |

#### Legend
⛔ Not Implemented &nbsp;&nbsp; ✅ Implemented

## Tools
| Tool                                                     | Description           |
|:---------------------------------------------------------|:----------------------|
| [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea) | IDE                   |
| [Drawio.com](https://www.drawio.com/)                    | UML and Sequence Diagram  |
| [Maven](https://maven.apache.org)                        | Dependency Management |
| [JUnit](https://junit.org/junit5)                        | Unit Testing          |
| [JavaFX](https://openjfx.io)                             | Graphical Library     |

## Testing
The JUnit tests cover:

100% of the model classes and 93% of the lines of the model. The few non-covered lines mainly deal with methods not used in tests due to their random nature. 

100% of the controller classes and 66% of the lines of the controller. The few non-covered lines mainly deal with methods not used in tests due to their random nature or methods used in establishing connections and keeping connections alive. 

## Jar
The jars are used to launch the software. A detailed guide on how to launch jar files is present in this section. 

To run the jar files, _Java SE Development Kit_ is required to be installed.
It can be downloaded from the official [_Oracle website_](https://www.oracle.com/java/technologies/downloads).\
To launch the game, it is necessary to open the server first.

### Server
The server can be run with the following command in a terminal window.

From Dir: ing-sw-2024-rocca-pasqual-paone-tonni
 ```
java -jar Deliverables\Game\server.jar
```
From Dir: Game
```
java -jar server.jar
 ```
### Client
The client can be run with the following command in a terminal window.

From Dir: ing-sw-2024-rocca-pasqual-paone-tonni
 ```
java -jar Deliverables\Game\client.jar 
```
From Dir: Game
```
java -jar client.jar
 ```

## Team
- [Federico Rocca](https://github.com/federock02) - `federico.rocca@mail.polimi.it`
- [Matteo Pasqual](https://github.com/matteopasqual02) - `matteoromilio.pasqual@mail.polimi.it`
- [Arianna Paone](https://github.com/AriannaPaone) - `arianna.paone@mail.polimi.it`
- [Alberto Tonni](https://github.com/ALBERTO0527) - `alberto.tonni@mail.polimi.it`
