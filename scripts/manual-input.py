#!/usr/bin/env python3
"""
Usage:
    python scripts/manual-input.py <latitude> <longitude> <pm2_5> <pm10> <no2> <so2> <o3>
Example:
    python scripts/manual-input.py 40.7128 -74.0060 12.5 25.0 40.0 20.0 33.0
"""

import sys
import subprocess
import json
import re
from datetime import datetime

SERVICE_NAME = "backend"
API_PORT = 8080
ENDPOINT = "/api/air-quality-measurements"

def print_usage():
    print("Usage: python scripts/manual-input.py <latitude> <longitude> <pm2_5> <pm10> <no2> <so2> <o3>")
    print("Example: python scripts/manual-input.py 40.7128 -74.0060 12.5 25.0 40.0 20.0 33.0")

def validate_float(value, name):
    if not re.match(r'^-?\d+(\.\d+)?$', value):
        print(f"Error: {name} must be a valid number.")
        return False
    return True

def check_docker_compose_running():
    try:
        result = subprocess.run(
            ["docker-compose", "ps", SERVICE_NAME],
            capture_output=True,
            text=True
        )
        return "Up" in result.stdout
    except Exception as e:
        print(f"Error checking Docker Compose status: {e}")
        return False

def create_payload(lat, lon, pm25, pm10, no2, so2, o3):
    timestamp = datetime.utcnow().isoformat()
    return {
        "latitude": float(lat),
        "longitude": float(lon),
        "pm25": float(pm25),
        "pm10": float(pm10),
        "no2": float(no2),
        "so2": float(so2),
        "o3": float(o3),
        "createdAt": timestamp
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
            print("\n✅ Data successfully sent!")
            if result.stdout.strip():
                print("Response:", result.stdout.strip())
            return True
        else:
            print("\n❌ Failed to send data.")
            if result.stderr.strip():
                print("Error:", result.stderr.strip())
            return False
    except Exception as e:
        print(f"\n❌ Error executing command: {e}")
        return False

def main():
    if len(sys.argv) != 8:
        print_usage()
        sys.exit(1)

    lat, lon, pm25, pm10, no2, so2, o3 = sys.argv[1:]

    for val, name in zip([lat, lon, pm25, pm10, no2, so2, o3],
                         ["Latitude", "Longitude", "PM2.5", "PM10", "NO2", "SO2", "O3"]):
        if not validate_float(val, name):
            sys.exit(1)

    if not check_docker_compose_running():
        print(f"Error: Docker Compose service '{SERVICE_NAME}' is not running.")
        print("Start it with: docker-compose up -d")
        sys.exit(1)

    payload = create_payload(lat, lon, pm25, pm10, no2, so2, o3)

    print("Sending full air quality data:")
    print(json.dumps(payload, indent=2))

    if not send_data_to_api(payload):
        sys.exit(1)

if __name__ == "__main__":
    main()
