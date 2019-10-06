#!/bin/bash

gnome-terminal --title="Simulation" --geometry 60x25+0+0 -e "python3 simulation.py"
sleep 3

for i in {1..10} 
do
	port=$((5010+$i))
	gnome-terminal --title="Cliente $i" -e "python3 client.py -p $port -t 600"
done