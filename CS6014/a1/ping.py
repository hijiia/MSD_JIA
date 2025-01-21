import re

def parse_ping_data(file_path):
    times = []

    # Read the file line by line
    with open(file_path, 'r') as file:
        for line in file:
            # Find the round trip time (last field, e.g., 'time=18.4 ms')
            match = re.search(r'time=(\d+\.\d+)', line)
            if match:
                times.append(float(match.group(1)))

    # Return the list of round-trip times
    return times

def calculate_average_delay(times):
    # Assume the min time is zero queuing delay
    min_time = min(times)
    queuing_delays = [time - min_time for time in times]
    avg_delay = sum(queuing_delays) / len(queuing_delays)
    return avg_delay

# Example usage
file_path = "/Users/jiagao/repository_jia/MSD_JIA/CS6014/a1/ping_data.txt"
ping_times = parse_ping_data(file_path)
average_delay = calculate_average_delay(ping_times)

print(f"Average round trip queuing delay: {average_delay:.3f} ms")