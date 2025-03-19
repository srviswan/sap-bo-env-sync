# JIRA to Excel VBA Scripts

This directory contains VBA scripts for fetching JIRA data and displaying it in Excel.

## GetJIRAData.bas

This VBA script provides functionality to fetch JIRA issues using the REST API and display them in Excel.

### Features

- Connects to JIRA REST API
- Uses Basic Authentication
- Parses JSON responses
- Formats data in Excel worksheets
- Includes error handling

### Usage Instructions

1. In Excel, press Alt+F11 to open the VBA editor
2. Insert a new module and paste the code from GetJIRAData.bas
3. Add reference to "Microsoft Scripting Runtime" (Tools > References)
4. For proper JSON parsing, download and import VBA-JSON library from:
   https://github.com/VBA-tools/VBA-JSON
5. Replace the ParseJson function with the one from VBA-JSON
6. Update the JIRA_BASE_URL, JIRA_USERNAME, and JIRA_API_TOKEN constants
7. Update the JQL query to match your project needs
8. Run the GetJIRAIssues sub

### Troubleshooting

If you encounter a Runtime Error 5 (Invalid procedure call or argument):
- Make sure the JIRA_BASE_URL is correctly formatted and ends with a slash
- Verify that all required references are added (Tools > References)
- Try using XMLHTTP instead of WinHttp if the error persists
- Check that your JQL query doesn't contain special characters that might cause issues
- Ensure you have the necessary permissions to make HTTP requests

For Authorization Header Issues:
- Make sure you're using valid credentials (email and API token for JIRA Cloud)
- Try generating the Base64 encoded string externally and hardcoding it for testing
- Check if your VBA security settings allow setting headers
- Try running Excel as administrator
- Some corporate environments block certain HTTP operations

## Note

While these VBA scripts can be useful, they may encounter issues with authentication, encoding, and HTTP requests. For a more reliable solution, consider using the Python scripts in the `../python` directory.
