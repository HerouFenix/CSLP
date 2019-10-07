#!/bin/bash

python3 simulation.py &

for i in {1..4} 
do
	port=$((5010+$i))
	python3 client.py -p $port -t 600 -c $i &
done

while :
do
	num_python_processes=$(ps a | grep -c "python3 client.py")
	if [ $num_python_processes -le 1 ]; then
		break;
	fi
	sleep 1
	echo $num_python_processes
done

ps a | grep -i simulation.py | awk {'print $1'} | xargs kill