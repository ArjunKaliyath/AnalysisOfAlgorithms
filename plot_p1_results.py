import pandas as pd
import matplotlib.pyplot as plt

# ===================== LOAD CSV FILES =====================

scenario1 = pd.read_csv("scenario1.csv")
scenario2 = pd.read_csv("scenario2.csv")

# Optional: sort data to ensure smooth curves
scenario1 = scenario1.sort_values(by=["S", "T"])
scenario2 = scenario2.sort_values(by=["Delta"])


# ===================== PLOT 1: RUNTIME VS MATRIX SIZE =====================

plt.figure(figsize=(10, 6))

x_labels = scenario1.apply(lambda row: f"{row['S']}x{row['T']}", axis=1)
plt.plot(x_labels, scenario1["Time"], marker="o")

plt.title("Scenario 1: Runtime vs Input Size")
plt.xlabel("Matrix Size (S × T)")
plt.ylabel("Time (ms)")
plt.grid(True)
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig("scenario1_runtime.png", dpi=300)
plt.show()


# ===================== PLOT 2: MEMORY VS MATRIX SIZE =====================

plt.figure(figsize=(10, 6))

plt.plot(x_labels, scenario1["Memory"], marker="o", color="red")

plt.title("Scenario 1: Memory Usage vs Input Size")
plt.xlabel("Matrix Size (S × T)")
plt.ylabel("Memory (MB)")
plt.grid(True)
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig("scenario1_memory.png", dpi=300)
plt.show()


# ===================== PLOT 3: SUBSTRING LENGTH VS DELTA =====================

plt.figure(figsize=(10, 6))

plt.plot(scenario2["Delta"], scenario2["Length"], marker="o", color="green")

plt.title("Scenario 2: Substring Length vs Delta")
plt.xlabel("Delta (Mismatch Penalty)")
plt.ylabel("Optimal Substring Length")
plt.grid(True)
plt.tight_layout()
plt.savefig("scenario2_length_vs_delta.png", dpi=300)
plt.show()


# ===================== OPTIONAL: TIME VS DELTA =====================

plt.figure(figsize=(10, 6))

plt.plot(scenario2["Delta"], scenario2["Time"], marker="o", color="purple")

plt.title("Scenario 2: Runtime vs Delta")
plt.xlabel("Delta")
plt.ylabel("Time (ms)")
plt.grid(True)
plt.tight_layout()
plt.savefig("scenario2_time_vs_delta.png", dpi=300)
plt.show()

# Compute matrix size labels: "SxT"
df = pd.read_csv("scenario2.csv")

df["SizeLabel"] = df["S"].astype(str) + "x" + df["T"].astype(str)

# ===================== AGGREGATE BY MATRIX SIZE =====================
# We average time and memory across the 10 delta values for each size
agg = df.groupby(["S", "T", "SizeLabel"]).agg(
    AvgTime=("Time", "mean"),
    AvgMemory=("Memory", "mean"),
    AvgLength=("Length", "mean")
).reset_index()

# Sort properly based on S,T
agg = agg.sort_values(by=["S", "T"])

# ===================== RUNTIME VS MATRIX SIZE =====================
plt.figure(figsize=(10,6))
plt.plot(agg["SizeLabel"], agg["AvgTime"], marker="o")

plt.title("Scenario 2: Average Runtime vs Matrix Size")
plt.xlabel("Matrix Size (S × T)")
plt.ylabel("Time (ms)")
plt.grid(True)
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig("scenario2_runtime_vs_size.png", dpi=300)
plt.show()

# ===================== MEMORY VS MATRIX SIZE =====================
plt.figure(figsize=(10,6))
plt.plot(agg["SizeLabel"], agg["AvgMemory"], marker="o", color="red")

plt.title("Scenario 2: Average Memory vs Matrix Size")
plt.xlabel("Matrix Size (S × T)")
plt.ylabel("Memory (MB)")
plt.grid(True)
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig("scenario2_memory_vs_size.png", dpi=300)
plt.show()

