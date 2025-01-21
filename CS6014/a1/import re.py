import re
import os

def parse_traceroute(file_path, output_file):
    with open(file_path, 'r') as file:
        lines = file.readlines()
    
    with open(output_file, 'w') as output:
        for line in lines:
            # Extract IP address
            ip_match = re.search(r"\((\d+\.\d+\.\d+\.\d+)\)", line)
            
            # Extract delay values
            delay_matches = re.findall(r"(\d+\.\d+)\s*ms", line)
            if ip_match and delay_matches:
                ip = ip_match.group(1)
                delays = list(map(float, delay_matches))
                avg_delay = sum(delays) / len(delays)
                output.write(f"{ip},{avg_delay:.3f}\n")
print("Current working directory:", os.getcwd())
parse_traceroute("/Users/jiagao/repository_jia/MSD_JIA/CS6014/a1/output1.txt", "parsed_output1.txt")
parse_traceroute("/Users/jiagao/repository_jia/MSD_JIA/CS6014/a1/output2.txt", "parsed_output2.txt")


