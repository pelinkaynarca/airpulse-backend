#!/usr/bin/env python3
"""
Usage:
    python scripts/auto-test.py --duration 60 --rate 2 --anomaly-chance 0.15 --cluster-mode

Options:
    --duration         Test duration in seconds (default: 60)
    --rate             Requests per second (default: 1)
    --anomaly-chance   Probability of anomaly per record (0.0–1.0, default: 0.1)
    --cluster-mode     Generate clustered data for better sudden increase testing
"""

import argparse
import random
import time
import subprocess
import json
from datetime import datetime, timezone

SERVICE_NAME = "backend"
API_PORT = 8080
ENDPOINT = "/api/air-quality-measurements"

# install curl check flag
curl_installed = False

# global cluster centers for generating measurements within 25km radius
CLUSTER_CENTERS = [
    # Europe
    {"lat": 51.5074, "lon": -0.1278, "name": "London"},
    {"lat": 48.8566, "lon": 2.3522, "name": "Paris"},
    {"lat": 52.5200, "lon": 13.4050, "name": "Berlin"},
    {"lat": 40.4168, "lon": -3.7038, "name": "Madrid"},
    {"lat": 41.0082, "lon": 28.9784, "name": "Istanbul"},

    # Asia
    {"lat": 35.6762, "lon": 139.6503, "name": "Tokyo"},
    {"lat": 39.9042, "lon": 116.4074, "name": "Beijing"},
    {"lat": 19.0760, "lon": 72.8777, "name": "Mumbai"},
    {"lat": 1.3521, "lon": 103.8198, "name": "Singapore"},

    # North America
    {"lat": 40.7128, "lon": -74.0060, "name": "New York"},
    {"lat": 34.0522, "lon": -118.2437, "name": "Los Angeles"},
    {"lat": 43.6532, "lon": -79.3832, "name": "Toronto"},
    {"lat": 19.4326, "lon": -99.1332, "name": "Mexico City"},

    # South America
    {"lat": -23.5505, "lon": -46.6333, "name": "São Paulo"},
    {"lat": -34.6037, "lon": -58.3816, "name": "Buenos Aires"},
    {"lat": 4.7110, "lon": -74.0721, "name": "Bogotá"},

    # Africa
    {"lat": -1.2921, "lon": 36.8219, "name": "Nairobi"},
    {"lat": -26.2041, "lon": 28.0473, "name": "Johannesburg"},
    {"lat": 30.0444, "lon": 31.2357, "name": "Cairo"},

    # Oceania
    {"lat": -33.8688, "lon": 151.2093, "name": "Sydney"},
    {"lat": -41.2924, "lon": 174.7787, "name": "Wellington"},
]

def ensure_curl_installed():
    global curl_installed

    if curl_installed:
        return True

    print("🔧 Checking if curl is installed...")

    # install curl if not found
    cmd = """
    if ! command -v curl &> /dev/null; then
        echo 'Installing curl...'
        apk update && apk add --no-cache curl
    else
        echo 'curl is already installed'
    fi
    """

    try:
        result = subprocess.run(
            ["docker-compose", "exec", "-T", SERVICE_NAME, "sh", "-c", cmd],
            capture_output=True,
            text=True
        )

        if result.returncode == 0:
            curl_installed = True
            print("   ✅ curl is ready")
            return True
        else:
            print(f"   ❌ Failed to install curl: {result.stderr}")
            return False
    except Exception as e:
        print(f"   ❌ Error: {str(e)}")
        return False

def create_random_payload(anomaly_chance, cluster_mode=False):
    if cluster_mode:
        center = random.choice(CLUSTER_CENTERS)
        lat = center["lat"] + random.uniform(-0.225, 0.225)
        lon = center["lon"] + random.uniform(-0.225, 0.225)
        location_name = center["name"]
    else:
        lat = round(random.uniform(-60.0, 70.0), 6)
        lon = round(random.uniform(-180.0, 180.0), 6)
        location_name = "Random"

    def generate_value(base):
        value = base + random.uniform(-5, 5)
        if random.random() < anomaly_chance:
            value = base * random.uniform(1.6, 2.0)
        return round(max(0, value), 2)

    parameters = ["PM2.5", "PM10", "NO2", "SO2", "O3"]
    parameter = random.choice(parameters)

    base_values = {
        "PM2.5": 20,
        "PM10": 40,
        "NO2": 30,
        "SO2": 20,
        "O3": 25
    }

    return {
        "parameter": parameter,
        "value": generate_value(base_values[parameter]),
        "latitude": lat,
        "longitude": lon,
        "createdAt": datetime.now(timezone.utc).isoformat(),
        "location": location_name
    }

def send_data_to_api(payload):
    location = payload.get('location', 'Unknown')
    print(f"📍 Sending: {payload['parameter']}={payload['value']} near {location} (lat={payload['latitude']:.4f}, lon={payload['longitude']:.4f})")

    api_payload = {k: v for k, v in payload.items() if k != "location"}
    payload_json = json.dumps(api_payload)

    cmd = f"""
    curl -X POST http://localhost:{API_PORT}{ENDPOINT} \\
        -H 'Content-Type: application/json' \\
        -s \\
        -d '{payload_json}'
    """

    try:
        result = subprocess.run(
            ["docker-compose", "exec", "-T", SERVICE_NAME, "sh", "-c", cmd],
            capture_output=True,
            text=True
        )

        if result.returncode == 0:
            print(f"   ✅ Sent successfully")
        else:
            print(f"   ❌ Failed: {result.stderr}")

        return result.returncode == 0
    except Exception as e:
        print(f"   ❌ Error: {str(e)}")
        return False

def run_test(duration, rate, anomaly_chance, cluster_mode):
    # make sure curl is installed
    if not ensure_curl_installed():
        print("❌ Cannot proceed without curl. Exiting.")
        return

    start_time = time.time()
    interval = 1 / rate if rate > 0 else 1

    print(f"\n🚀 Starting test: {duration}s | {rate} req/s | {anomaly_chance * 100}% anomalies")
    if cluster_mode:
        print(f"📍 Using cluster mode with {len(CLUSTER_CENTERS)} global cities")
    else:
        print("📍 Using random locations across the globe")
    print()

    count = 0
    successful = 0

    while time.time() - start_time < duration:
        payload = create_random_payload(anomaly_chance, cluster_mode)
        if send_data_to_api(payload):
            successful += 1
        count += 1

        print()
        time.sleep(interval)

    print(f"\n✨ Test completed. Sent {count} measurements ({successful} successful)")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Airpulse Auto Test Script")
    parser.add_argument("--duration", type=int, default=60, help="Duration in seconds")
    parser.add_argument("--rate", type=float, default=1, help="Requests per second")
    parser.add_argument("--anomaly-chance", type=float, default=0.1, help="Anomaly probability (0.0–1.0)")
    parser.add_argument("--cluster-mode", action="store_true", help="Generate clustered data for testing")
    args = parser.parse_args()

    run_test(args.duration, args.rate, args.anomaly_chance, args.cluster_mode)