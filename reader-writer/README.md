
## Overview

This repository presents implementations of the reader-writer concurrency problem using buffer techniques. The approaches vary by their use of synchronization primitives and design strategy to manage shared data. The goal is to explore how different methods fare under concurrent access, emphasizing performance, simplicity, and correctness.

## Implementation Details

### Double Buffer without Atomic Variables
- **Mechanics:**  
  Utilizes two buffers to alternate between read and write processes.  
- **Considerations:**  
  Since no atomic operations are used, the design relies on controlled access to mitigate race conditions.

### Double Buffer with Atomic Variables
- **Mechanics:**  
  Similar to the standard double buffer approach but uses atomic operations (e.g., atomic flags or counters) to synchronize access.
- **Considerations:**  
  Aims to carefully manage timing to reduce the risk of data overwriting, though rare cases may still result in overwrites due to subtleties in timing and thread scheduling.

### Multiple Buffer with Garbage Collection
- **Mechanics:**  
  Creates multiple buffers dynamically with the intent of letting unmanaged buffers be reclaimed by the language's garbage collector.
- **Considerations:**  
  Offers a flexible mechanism to handle buffers more fluidly. Helps in scenarios where buffering can be more granular, though it depends on the underlying garbage collector's efficiency.

---
