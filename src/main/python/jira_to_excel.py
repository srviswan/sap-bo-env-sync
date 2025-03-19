#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
JIRA to Excel Export Tool

This script fetches data from JIRA using the REST API and exports it to Excel.
It provides a more reliable alternative to VBA scripts for JIRA data extraction.
"""

import os
import argparse
import json
import pandas as pd
import requests
from requests.auth import HTTPBasicAuth
from datetime import datetime

# Default configuration - override with command line arguments
DEFAULT_CONFIG = {
    "jira_base_url": "https://your-jira-instance.atlassian.net/rest/api/2/",
    "jql_query": "project = YourProjectKey ORDER BY updated DESC",
    "max_results": 100,
    "output_file": "jira_export.xlsx"
}

def parse_args():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Export JIRA issues to Excel')
    
    parser.add_argument('--url', dest='jira_base_url', 
                        help='JIRA base URL (e.g., https://your-jira-instance.atlassian.net/rest/api/2/)')
    
    parser.add_argument('--jql', dest='jql_query',
                        help='JQL query (e.g., "project = YourProjectKey")')
    
    parser.add_argument('--max', dest='max_results', type=int,
                        help=f'Maximum number of results (default: {DEFAULT_CONFIG["max_results"]})')
    
    parser.add_argument('--output', dest='output_file',
                        help=f'Output Excel file (default: {DEFAULT_CONFIG["output_file"]})')
    
    parser.add_argument('--email', dest='email', required=True,
                        help='JIRA email address')
    
    parser.add_argument('--token', dest='api_token', required=True,
                        help='JIRA API token')
    
    args = parser.parse_args()
    
    # Use default values for any missing arguments
    for key, default_value in DEFAULT_CONFIG.items():
        if getattr(args, key, None) is None:
            setattr(args, key, default_value)
    
    return args

def fetch_jira_data(base_url, jql_query, max_results, email, api_token):
    """Fetch data from JIRA API."""
    print(f"Fetching data from JIRA with query: {jql_query}")
    
    # Construct the search URL
    search_url = f"{base_url.rstrip('/')}/search"
    
    # Set up authentication
    auth = HTTPBasicAuth(email, api_token)
    
    # Set up request parameters
    params = {
        "jql": jql_query,
        "maxResults": max_results,
        "fields": "key,summary,status,priority,assignee,reporter,created,updated,description,issuetype"
    }
    
    # Set up headers
    headers = {
        "Accept": "application/json"
    }
    
    try:
        # Make the API request
        response = requests.get(
            search_url,
            auth=auth,
            headers=headers,
            params=params
        )
        
        # Check if the request was successful
        response.raise_for_status()
        
        # Parse the JSON response
        data = response.json()
        
        print(f"Successfully fetched {len(data.get('issues', []))} issues from JIRA")
        return data
    
    except requests.exceptions.RequestException as e:
        print(f"Error fetching data from JIRA: {e}")
        if hasattr(e, 'response') and e.response:
            print(f"Response status code: {e.response.status_code}")
            print(f"Response body: {e.response.text}")
        raise

def process_jira_data(data):
    """Process JIRA data into a pandas DataFrame."""
    if not data or 'issues' not in data or not data['issues']:
        print("No issues found in JIRA response")
        return pd.DataFrame()
    
    # Extract issues
    issues = data['issues']
    
    # Prepare data for DataFrame
    processed_data = []
    
    for issue in issues:
        issue_data = {
            'Key': issue['key'],
            'Summary': issue['fields'].get('summary', ''),
            'Status': issue['fields'].get('status', {}).get('name', ''),
            'Priority': issue['fields'].get('priority', {}).get('name', ''),
            'Issue Type': issue['fields'].get('issuetype', {}).get('name', ''),
            'Assignee': issue['fields'].get('assignee', {}).get('displayName', ''),
            'Reporter': issue['fields'].get('reporter', {}).get('displayName', ''),
            'Created': issue['fields'].get('created', ''),
            'Updated': issue['fields'].get('updated', ''),
            'Description': issue['fields'].get('description', '')
        }
        
        processed_data.append(issue_data)
    
    # Convert to DataFrame
    df = pd.DataFrame(processed_data)
    
    # Convert date strings to datetime objects
    for date_field in ['Created', 'Updated']:
        if date_field in df.columns:
            df[date_field] = pd.to_datetime(df[date_field]).dt.strftime('%Y-%m-%d %H:%M:%S')
    
    return df

def export_to_excel(df, output_file):
    """Export DataFrame to Excel."""
    if df.empty:
        print("No data to export")
        return False
    
    try:
        # Create directory if it doesn't exist
        output_dir = os.path.dirname(output_file)
        if output_dir and not os.path.exists(output_dir):
            os.makedirs(output_dir)
        
        # Export to Excel
        with pd.ExcelWriter(output_file, engine='openpyxl') as writer:
            df.to_excel(writer, sheet_name='JIRA Issues', index=False)
            
            # Auto-adjust column widths
            worksheet = writer.sheets['JIRA Issues']
            for i, column in enumerate(df.columns):
                max_length = max(
                    df[column].astype(str).map(len).max(),
                    len(column)
                ) + 2
                # Excel has a maximum column width of 255
                worksheet.column_dimensions[chr(65 + i)].width = min(max_length, 255)
        
        print(f"Successfully exported {len(df)} issues to {output_file}")
        return True
    
    except Exception as e:
        print(f"Error exporting to Excel: {e}")
        return False

def main():
    """Main function."""
    # Parse command line arguments
    args = parse_args()
    
    try:
        # Fetch data from JIRA
        jira_data = fetch_jira_data(
            args.jira_base_url,
            args.jql_query,
            args.max_results,
            args.email,
            args.api_token
        )
        
        # Process data
        df = process_jira_data(jira_data)
        
        # Export to Excel
        success = export_to_excel(df, args.output_file)
        
        if success:
            print(f"JIRA data successfully exported to {args.output_file}")
        else:
            print("Failed to export JIRA data")
    
    except Exception as e:
        print(f"Error: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())
