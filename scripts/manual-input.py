#!/usr/bin/env python3
"""
Manual input script for adding air quality measurements

Usage:
    python scripts/manual-input.py <latitude> <longitude> <parameter> <value>
Example:
    python scripts/manual-input.py 40.7128 -74.0060 PM2.5 25.0
"""

import sys
import subprocess
import json
import re
from datetime import datetime, timezone

SERVICE_NAME = "backend"
API_PORT = 8080
ENDPOINT = "/api/air-quality-measurements"

VALID_PARAMETERS = ["PM2.5", "PM10", "NO2", "SO2", "O3"]

def print_usage():
    print("Usage: python scripts/manual-input.py <latitude> <longitude> <parameter> <value>")
    print("Example: python scripts/manual-input.py 40.7128 -74.0060 PM2.5 25.0")
    print(f"Valid parameters: {', '.join(VALID_PARAMETERS)}")

def validate_float(value, name):
    try:
        float(value)
        return True
    except ValueError:
        print(f"Error: {name} must be a valid number.")
        return False

def validate_parameter(parameter):
    if parameter not in VALID_PARAMETERS:
        print(f"Error: Invalid parameter '{parameter}'")
        print(f"Valid parameters are: {', '.join(VALID_PARAMETERS)}")
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

def create_payload(lat, lon, parameter, value):
    payload = {
        "parameter": parameter,
        "value": float(value),
        "latitude": float(lat),
        "longitude": float(lon),
        "createdAt": datetime.now(timezone.utc).isoformat()
    }
    return payload

def send_data_to_api(payload):
    payload_json = json.dumps(payload)

    # install curl if it's not available
    install_cmd = f"""
    if ! command -v curl &> /dev/null; then
        echo 'Installing curl...'
        apk update && apk add --no-cache curl
    fi
    """

    try:
        subprocess.run(
            ["docker-compose", "exec", "-T", SERVICE_NAME, "sh", "-c", install_cmd],
            capture_output=True,
            text=True
        )
    except Exception as e:
        print(f"Warning: Could not install curl: {e}")

    # Send the request
    cmd = f"""
    curl -X POST http://localhost:{API_PORT}{ENDPOINT} \\
        -H 'Content-Type: application/json' \\
        -w "\\nHTTP Status: %{{http_code}}\\n" \\
        -s \\
        -d '{payload_json}'
    """

    try:
        result = subprocess.run(
            ["docker-compose", "exec", "-T", SERVICE_NAME, "sh", "-c", cmd],
            capture_output=True,
            text=True
        )

        print(f"\n📊 Response:")
        print(result.stdout)

        if result.returncode == 0:
            if "HTTP Status: 201" in result.stdout:
                print("\n✅ Measurement created successfully!")
                return True
            elif "HTTP Status: 202" in result.stdout:
                print("\n✅ Measurement queued successfully!")
                return True
            else:
                print("\n⚠️  Check the response above for details.")
                return False
        else:
            print("\n❌ Failed to send measurement.")
            if result.stderr.strip():
                print("Error:", result.stderr.strip())
            return False
    except Exception as e:
        print(f"\n❌ Error: {e}")
        return False

def main():
    if len(sys.argv) != 5:
        print_usage()
        sys.exit(1)

    lat, lon, parameter, value = sys.argv[1:]

    # Validate inputs
    if not validate_float(lat, "Latitude"):
        sys.exit(1)
    if not validate_float(lon, "Longitude"):
        sys.exit(1)
    if not validate_float(value, "Value"):
        sys.exit(1)
    if not validate_parameter(parameter):
        sys.exit(1)

    # Validate ranges
    lat_f, lon_f, value_f = float(lat), float(lon), float(value)
    if not (-90 <= lat_f <= 90):
        print("Error: Latitude must be between -90 and 90")
        sys.exit(1)
    if not (-180 <= lon_f <= 180):
        print("Error: Longitude must be between -180 and 180")
        sys.exit(1)
    if value_f < 0:
        print("Error: Value must be greater than or equal to 0")
        sys.exit(1)

    if not check_docker_compose_running():
        print(f"Error: Docker Compose service '{SERVICE_NAME}' is not running.")
        print("Start it with: docker-compose up -d")
        sys.exit(1)

    payload = create_payload(lat, lon, parameter, value)

    print(f"\nSending measurement:")
    print(json.dumps(payload, indent=2))

    if not send_data_to_api(payload):
        print("\n🔍 To debug further, check the backend logs with:")
        print("docker-compose logs backend -f")
        sys.exit(1)

if __name__ == "__main__":
    main()