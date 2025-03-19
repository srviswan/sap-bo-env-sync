# JIRA to Excel Export Tool

This Python script provides a reliable way to fetch data from JIRA using the REST API and export it to Excel format.

## Features

- Fetches JIRA issues based on a JQL query
- Exports data to a formatted Excel file
- Handles authentication securely
- Provides detailed error reporting
- Supports command-line arguments for flexibility

## Requirements

- Python 3.6+
- Required packages (install using `pip install -r requirements.txt`):
  - pandas
  - requests
  - openpyxl

## Usage

1. Install the required packages:
   ```
   pip install -r requirements.txt
   ```

2. Run the script with your JIRA credentials:
   ```
   python jira_to_excel.py --email your.email@example.com --token your_api_token
   ```

3. Additional optional arguments:
   ```
   python jira_to_excel.py --email your.email@example.com --token your_api_token --url https://your-jira-instance.atlassian.net/rest/api/2/ --jql "project = YourProject" --max 200 --output jira_data.xlsx
   ```

## Command Line Arguments

- `--email`: Your JIRA email address (required)
- `--token`: Your JIRA API token (required)
- `--url`: JIRA base URL (default: https://your-jira-instance.atlassian.net/rest/api/2/)
- `--jql`: JQL query (default: "project = YourProjectKey ORDER BY updated DESC")
- `--max`: Maximum number of results (default: 100)
- `--output`: Output Excel file path (default: jira_export.xlsx)

## How to Get a JIRA API Token

1. Log in to https://id.atlassian.com/manage-profile/security/api-tokens
2. Click "Create API token"
3. Give your token a name and click "Create"
4. Copy the token (you won't be able to see it again)

## Advantages Over VBA

- More reliable authentication handling
- Better error reporting
- No issues with Base64 encoding or HTTP requests
- Easier to maintain and extend
- Can be scheduled using cron or Task Scheduler
- More robust JSON parsing
