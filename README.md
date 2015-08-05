# CS150-graph-optimization
The final project for the CS150 Algorithms class, on graph optimization. This project is written in Java.

For the technical report, see [P3 report](./P3-report/Keng-P3-report.pdf).

## Abstract
This project is to simulate a fuel distribution network: Given a graph with fuel depots and gas stations as vertices, with weight edges, for each station find a fuel depot to service it such that the total number of miles travelled and the longest distance from station to depot are minimized.
We present a solution by proving two lemmas1, and using them to construct the Nearest-Neighbor paths for a given graph, such that each station is guaranteed (if not isolated) service by a closest fuel depot; and the sum of the distances of all paths is minimized.

## Conclusion
In this project we provided a solution by defining a variation of Nearest-Neighbor Graph. We proved two lemmas which allow us to device an algorithm that yields shortest paths from depot to stations which satisfy all three project requirements.
Moreover, we provided analysis of an implementation, showed that its performance is cool, and the result is as optimal(best) as proven by the lemmas.