# NewscastProtocol
It is the final project of the P2P and Blockchain course and it consists of the implementation of the Newscast (scalable, gossiping) protocol.

The project has been written in Java.

As will be more evident by reading the `report.pdf` file, the results analysis phase is very consistent.
The simulations have been carried out by emulating just one execution of the protocol for all the nodes of the network.

The simulations have been performed by starting with two different kinds of network graphs, that is the free scale and the grid network.
The protocol requires also the number of cache lines present for each peer.
The various simulations carried out have been the following ones:

| network | cache lines | peers number |
|---------|-------------|--------------|
| scale free | 20 | 1000  |
| scale free | 20 | 2500  |
| scale free | 20 | 5000  |
| scale free | 20 | 7500  |
| scale free | 20 | 10000 |
| scale free | 35 | 1000  |
| scale free | 35 | 2500  |
| scale free | 35 | 5000  |
| scale free | 35 | 7500  |
| scale free | 35 | 10000 |
| scale free | 50 | 1000  |
| scale free | 50 | 2500  |
| scale free | 50 | 5000  |
| scale free | 50 | 7500  |
| scale free | 50 | 10000 |
| grid | 20 | 10x100 |
| grid | 20 | 25x100 |
| grid | 20 | 50x100 |
| grid | 20 | 75x100 |
| grid | 20 | 100x100 |
| grid | 20 | 100x10 | 
| grid | 35 | 10x100 |
| grid | 35 | 25x100 |
| grid | 35 | 50x100 |
| grid | 35 | 75x100 |
| grid | 35 | 100x100 |
| grid | 35 | 100x10 |
| grid | 50 | 10x100 |
| grid | 50 | 25x100 |
| grid | 50 | 50x100 |
| grid | 50 | 75x100 |
| grid | 50 | 100x100 |
| grid | 50 | 100x10 |

As previously said, in the `report.pdf` file, the results analysis phase is very extensive, since the test cases are very consistent.
