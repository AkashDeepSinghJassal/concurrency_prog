import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Data for buffer size 10^8
double_buffer = {
    'AvgWriter1Time': 0.000307,
    'AvgWriter2Time': 0.000395,
    'AvgReaderTime': 0.000408
}

multiple_buffer = {
    'AvgWriter1Time': 20.275497,
    'AvgWriter2Time': 20.301725,
    'AvgReaderTime': 0.000035
}

# Create DataFrame
df = pd.DataFrame({
    'Double Buffer': list(double_buffer.values()),
    'Multiple Buffer': list(multiple_buffer.values())
}, index=list(double_buffer.keys()))

print("Data:")
print(df)

# Plotting
fig, ax = plt.subplots(figsize=(12, 8))

# Set width of bars
barWidth = 0.35
positions = np.arange(len(df.index))

# Create bars
double_bars = ax.bar(positions - barWidth/2, df['Double Buffer'], barWidth, label='Double Buffer', 
                     color='skyblue', edgecolor='black', alpha=0.8)
multiple_bars = ax.bar(positions + barWidth/2, df['Multiple Buffer'], barWidth, label='Multiple Buffer', 
                       color='lightgreen', edgecolor='black', alpha=0.8)

# Add data labels on bars
def add_labels(bars):
    for bar in bars:
        height = bar.get_height()
        ax.annotate(f'{height:.6f}',
                    xy=(bar.get_x() + bar.get_width() / 2, height),
                    xytext=(0, 3),  # 3 points vertical offset
                    textcoords="offset points",
                    ha='center', va='bottom', fontsize=9, rotation=45)

add_labels(double_bars)
add_labels(multiple_bars)

# Add some text for labels, title and custom x-axis tick labels
ax.set_ylabel('Average Time (ms)', fontsize=12)
ax.set_title('Comparison: Double Buffer vs Multiple Buffer (10^8 buffer size)', fontsize=14)
ax.set_xticks(positions)
ax.set_xticklabels(df.index, fontsize=10)
ax.legend()

# Create a second axis for a log scale version
ax2 = ax.twinx()
ax2.set_yscale('log')
ax2.set_ylabel('Average Time (ms) - Log Scale', fontsize=12)

# Add grid
ax.grid(True, linestyle='--', alpha=0.6)

plt.tight_layout()
plt.savefig('buffer-comparison.png', dpi=300)
plt.show()

# Create another figure with log scale for better visualization of differences
plt.figure(figsize=(12, 8))
ax_log = df.plot(kind='bar', logy=True, rot=0, figsize=(12, 8), width=0.8)

# Add data labels on bars
for container in ax_log.containers:
    ax_log.bar_label(container, fmt='%.6f', fontsize=9, rotation=45)

plt.title('Comparison: Double Buffer vs Multiple Buffer - Log Scale (10^8 buffer size)', fontsize=14)
plt.ylabel('Average Time (ms) - Log Scale', fontsize=12)
plt.grid(True, which='both', linestyle='--', alpha=0.6)
plt.tight_layout()
plt.savefig('buffer-comparison-log.png', dpi=300)
plt.show()
