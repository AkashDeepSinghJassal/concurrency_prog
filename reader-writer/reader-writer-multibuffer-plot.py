import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

buffer_sizes = [1000, 10000, 100000, 1000000, 10000000, 100000000]
writer1_times = [0.000751, 0.005089, 0.031594, 0.139189, 1.415452, 20.275497]
writer2_times = [0.000817, 0.005168, 0.031250, 0.139173, 1.414249, 20.301725]
reader_times = [0.000407, 0.000305, 0.000322, 0.000254, 0.000130, 0.000035]

# Create DataFrame
df = pd.DataFrame({
    'BufferSize': buffer_sizes,
    'Writer1': writer1_times,
    'Writer2': writer2_times,
    'Reader': reader_times
})


print("Data:")
print(df)

df_melted = pd.melt(df, id_vars=['BufferSize'], var_name='EntityType', value_name='AverageTime(ms)')

# Plotting
plt.figure(figsize=(10, 6))

# Define different line styles and markers
line_styles = ['--', '-.', ':']
markers = ['o', 's', '^']

# Create custom plot with different line styles and markers
for i, entity in enumerate(df_melted['EntityType'].unique()):
    subset = df_melted[df_melted['EntityType'] == entity]
    line = plt.plot(subset['BufferSize'], subset['AverageTime(ms)'], 
                    label=entity, 
                    linestyle=line_styles[i % len(line_styles)],
                    marker=markers[i % len(markers)],
                    markersize=8)
    
    # Add point value annotations
    for x, y in zip(subset['BufferSize'], subset['AverageTime(ms)']):
        plt.annotate(f'{y:.6f}', 
                    (x, y), 
                    textcoords="offset points",
                    xytext=(0,10), 
                    ha='center',
                    fontsize=8)

plt.xscale('log')
plt.yscale('log')
plt.xlabel('Buffer length')
plt.ylabel('Average Time (ms)')
plt.title('Multi buffer - Average Operation Time vs. Buffer Size')
plt.grid(True, which="both", ls="--")
plt.legend(title='Entity Type')
plt.tight_layout()

plt.savefig('reader-writer-comparision.png', dpi=300)
plt.show()
