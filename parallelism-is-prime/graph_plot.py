import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns


def process_csv_file(file_path, impl_name=None):
    """Read a CSV file and add an implementation name column"""
    df = pd.read_csv(file_path)
    
    df['Implementation'] = impl_name
    return df


# Define the list of CSV files and their display names
csv_files = [
    {'path': 'prime_results_partition_data_local_counter.csv', 'name': 'Partition local counter'},
    {'path': 'prime_results_partition_same_cache_line.csv', 'name': 'Partition Same Cache Line'},
    {'path': 'prime_results_shared_counter_no_mutex.csv', 'name': 'Shared Counter No Mutex'},
    {'path': 'prime_results_shared_counter_with_mutex.csv', 'name': 'Shared Counter Mutex'}
]

# Process all CSV files
dataframes = []
for csv_file in csv_files:
    try:
        df = process_csv_file(csv_file['path'], csv_file['name'])
        dataframes.append(df)
        print(f"Successfully loaded {csv_file['path']}")
    except Exception as e:
        print(f"Error loading {csv_file['path']}: {e}")

# Check if we have any dataframes to plot
if not dataframes:
    print("No data to plot. Please check your CSV files.")
    exit()

# Combine all dataframes
combined_df = pd.concat(dataframes)

# Create the plot
plt.figure(figsize=(12, 6))
line_plot = sns.lineplot(
    data=combined_df,
    x='ThreadCount',
    y='TimeTaken(ms)',
    hue='Implementation',
    markers=True,
    style='Implementation',
    linewidth=2.5
)

# Add annotations
plt.title('Performance Comparison: Execution Time vs Thread Count', fontsize=16)
plt.xlabel('Number of Threads', fontsize=12)
plt.ylabel('Time Taken (ms)', fontsize=12)
plt.grid(True)

# Get the range of thread counts from the data
thread_counts = sorted(combined_df['ThreadCount'].unique())
plt.xticks(thread_counts)

plt.legend(title='Implementation')

# Add a log scale option if data varies significantly in magnitude
if combined_df['TimeTaken(ms)'].max() / combined_df['TimeTaken(ms)'].min() > 100:
    plt.yscale('log')
    plt.ylabel('Time Taken (ms) - Log Scale', fontsize=12)

# Show the plot
plt.tight_layout()
plt.savefig('performance_comparison.png', dpi=300)
plt.show()

# Print statistics for each implementation
print("\nPerformance Statistics:")
print("-" * 50)

for df in dataframes:
    impl_name = df['Implementation'].iloc[0]
    print(f"\nSpeed-up analysis for {impl_name}:")
    print(f"Single thread time: {df['TimeTaken(ms)'].iloc[0]:.2f} ms")
    
    min_idx = df['TimeTaken(ms)'].idxmin()
    best_thread_count = df.loc[min_idx, 'ThreadCount']
    best_time = df['TimeTaken(ms)'].min()
    
    print(f"Best time ({best_thread_count} threads): {best_time:.2f} ms")
    print(f"Speed-up factor: {df['TimeTaken(ms)'].iloc[0]/best_time:.2f}x")
    