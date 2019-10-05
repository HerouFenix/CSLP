#!/usr/bin/env bash
gnome-terminal --title="Ring" --geometry 60x25+0+0 -e "python3 simulation.py" & 
sleep 3

for i in {1..20} 
do
	port=$((5010+$i))
	gnome-terminal --title="Cliente $i" -e "python3 client.py -p $port -t 600" &
done
