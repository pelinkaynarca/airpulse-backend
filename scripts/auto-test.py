#!/usr/bin/env python3
"""
Usage:
    python scripts/auto-test.py --duration 60 --rate 2 --anomaly-chance 0.15

Options:
    --duration         Test duration in seconds (default: 60)
    --rate             Requests per second (default: 1)
    --anomaly-chance   Probability of anomaly per record (0.0–1.0, default: 0.1)
"""

import argparse
import random
import time
import subprocess
import json
from datetime import datetime

SERVICE_NAME = "backend"
API_PORT = 8080
ENDPOINT = "/api/air-quality-measurements"

def create_random_payload(anomaly_chance):
    lat = round(random.uniform(35.0, 42.0), 6)
    lon = round(random.uniform(26.0, 45.0), 6)

    def generate_value(base, anomaly_boost=0):
        value = base + random.uniform(-5, 5)
        if random.random() < anomaly_chance:
            value += anomaly_boost + random.uniform(10, 30)
        return round(max(0, value), 2)

    return {
        "latitude": lat,
        "longitude": lon,
        "pm25": generate_value(20, 35),
        "pm10": generate_value(40, 50),
        "no2": generate_value(30, 25),
        "so2": generate_value(20, 15),
        "o3": generate_value(25, 30),
        "createdAt": datetime.utcnow().isoformat()
    }

def send_data_to_api(payload):
    payload_json = json.dumps(payload)

    cmd = f"""
    if ! command -v curl &> /dev/null; then
        echo 'Installing curl...'
        apk add --no-cache curl
    fi

    curl -X POST http://localhost:{API_PORT}{ENDPOINT} \\
        -H 'Content-Type: application/json' \\
        -d '{payload_json}'
    """

    try:
        result = subprocess.run(
            ["docker-compose", "exec", "-T", SERVICE_NAME, "sh", "-c", cmd],
            capture_output=True,
            text=True
        )
        if result.returncode == 0:
            print("✅ Data sent:", payload)
        else:
            print("❌ Error sending data:", result.stderr.strip())
    except Exception as e:
        print("❌ Exception while sending data:", e)

def run_test(duration, rate, anomaly_chance):
    start_time = time.time()
    interval = 1 / rate if rate > 0 else 1

    while time.time() - start_time < duration:
        payload = create_random_payload(anomaly_chance)
        send_data_to_api(payload)
        time.sleep(interval)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Airpulse Auto Test Script")
    parser.add_argument("--duration", type=int, default=60, help="Duration in seconds")
    parser.add_argument("--rate", type=float, default=1, help="Requests per second")
    parser.add_argument("--anomaly-chance", type=float, default=0.1, help="Anomaly probability (0.0–1.0)")
    args = parser.parse_args()

    print(f"\n🚀 Starting test: {args.duration}s | {args.rate} req/s | {args.anomaly_chance * 100}% anomalies")
    run_test(args.duration, args.rate, args.anomaly_chance)
