# Task 1 - Das Experiment

## Overview

This task investigated concurrent access to different `Map` implementations in Java.

Scenario:
- one thread continuously adds elements to the map;
- another thread iterates over the values and calculates their sum.

---

## Step 1 - `HashMap`

Used a regular `HashMap<Integer, Integer>`.

**Result:**  
`ConcurrentModificationException` occurred.

**Reason:**  
`HashMap` is not thread-safe. One thread modified the map while another thread iterated over it.

**Conclusion:**  
Regular `HashMap` is unsafe for concurrent write + iteration.

---

## Step 2 - `Collections.synchronizedMap(...)`

Replaced `HashMap` with:

```java
Collections.synchronizedMap(new HashMap<Integer, Integer>())
```
**Result:** 
`ConcurrentModificationException` still occurred.

**Reason:**
`synchronizedMap` synchronizes individual operations such as `put()` and `get()`, but it does not automatically make iteration thread-safe.

**Conclusion:**
Replacing `HashMap` with `Collections.synchronizedMap(...)` alone is not enough.

---

## Step 3 - Synchronized Iteration

**Implementation:**
Wrapped iteration in a synchronized block:

```java
synchronized (map) {
    for (Integer value : map.values()) {
        sum += value;
    }
}
```
**Result:** 
`ConcurrentModificationException` disappeared.

**Reason:**
Iteration and modification could no longer happen at the same time.

**Conclusion:**
For `Collections.synchronizedMap(...)`, iteration must be synchronized manually.

---

## Step 4 - `ConcurrentHashMap`

**Implementation:**
Replaced the map with:
```java
new ConcurrentHashMap<Integer, Integer>()
```
**Result:** 
No `ConcurrentModificationException` occurred.

**Reason:**
`ConcurrentHashMap` supports concurrent access.
Its iterators are weakly consistent and safe during modification.

**Conclusion:**
`ConcurrentHashMap` is a better solution for concurrent read/write access.

---

## Step 5 - Custom Map Implementations

**Implementation:**
Created two custom wrappers around `HashMap`.

*`CustomMapWithoutSync`*
Uses internal `HashMap`
No synchronization

**Result:** 
`ConcurrentModificationException` occurred.

*`CustomMapSync`*
Both `put()` and `sumValues()` were synchronized.

**Result:** 
No exception occurred.

**Conclusion:**
A custom wrapper becomes thread-safe only when access is properly synchronized.

**Final Conclusion:**
The problem was caused by concurrent modification of a non-thread-safe map during iteration.

**Main results:**

`HashMap` failed
`synchronizedMap` alone was not enough
`synchronized` iteration fixed the issue
`ConcurrentHashMap` worked correctly
`custom synchronized` wrapper also worked correctly

---

## Task 1 Step 6 - Perfomance

**Results**

- **Collections.synchronizedMap**
  - Write ops: 88,734
  - Read ops: 23,028
  - Total ops: 111,762
  - Ops/sec: 22,352.4

- **ConcurrentHashMap**
  - Write ops: 25,155,824
  - Read ops: 38
  - Total ops: 25,155,862
  - Ops/sec: 5,031,172.4

- **CustomThreadSafeMap**
  - Write ops: 389,024
  - Read ops: 2,175
  - Total ops: 391,199
  - Ops/sec: 78,239.8

### Short Conclusion

`ConcurrentHashMap` showed the highest total throughput in this benchmark.  
However, the number of read operations was very low because the map grew very quickly, and each iteration over values became more expensive.

`Collections.synchronizedMap` showed the lowest throughput because iteration required explicit synchronization, which blocked concurrent writes.

`CustomThreadSafeMap` performed better than `Collections.synchronizedMap` in total operations, but it still used a single lock and therefore limited concurrency.

### Final Note

This benchmark is simplified because the map size continuously increased during the test.  
Therefore, the results reflect not only synchronization strategy, but also the cost of iterating over a growing map.

---








