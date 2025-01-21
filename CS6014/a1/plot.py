import matplotlib.pyplot as plt

def plot_traceroute(file1, file2):
    def read_data(file):
        x, y = [], []
        with open(file, 'r') as f:
            for line in f:
                ip, avg_delay = line.strip().split(',')
                x.append(ip)
                y.append(float(avg_delay))
        return x, y

    x1, y1 = read_data(file1)
    x2, y2 = read_data(file2)

    plt.figure(figsize=(12, 6))
    plt.plot(x1, y1, marker='o', label='Run 1')
    plt.plot(x2, y2, marker='o', label='Run 2')
    plt.xticks(rotation=45)
    plt.xlabel('IP Address')
    plt.ylabel('Average Delay (ms)')
    plt.title('Traceroute Delay Analysis')

    plt.legend()
    plt.tight_layout()
    plt.savefig('traceroute_graph.pdf')
    plt.show()

plot_traceroute("/Users/jiagao/repository_jia/MSD_JIA/CS6014/a1/parsed_output1.txt", 
                "/Users/jiagao/repository_jia/MSD_JIA/CS6014/a1/parsed_output2.txt")