import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("results.csv")

# Create labels like "10x10"
df["label"] = df["rows"].astype(str) + "x" + df["cols"].astype(str)

# ---- Runtime Plot ----
plt.figure(figsize=(8,5))
plt.plot(df["label"], df["time_ms"], marker="o")
plt.xlabel("Matrix Size (m × n)")
plt.ylabel("Runtime (ms)")
plt.title("DP Runtime vs Matrix Size")
plt.grid(True)
plt.tight_layout()
plt.savefig("runtime_plot.png")
plt.show()

# ---- Memory Plot ----
plt.figure(figsize=(8,5))
plt.plot(df["label"], df["memory_MB"], marker="o")
plt.xlabel("Matrix Size (m × n)")
plt.ylabel("Memory Usage (MB)")
plt.title("Memory Usage vs Matrix Size")
plt.grid(True)
plt.tight_layout()
plt.savefig("memory_plot.png")
plt.show()
