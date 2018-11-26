javac VideoCreator.java

output_dir=output
fps=25
resolution=480p
title=test
duration=300

rm -rf $output_dir


java VideoCreator $output_dir $fps $resolution $title $duration 

ffmpeg -start_number 0 -i $output_dir/images/%d.png -c:v libx264 -vf "fps=$fps,format=yuv420p" $output_dir/out.mp4

