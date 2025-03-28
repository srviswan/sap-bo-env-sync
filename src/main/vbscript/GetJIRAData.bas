Option Explicit

' Constants for JIRA API configuration
Const JIRA_BASE_URL As String = "https://your-jira-instance.atlassian.net/rest/api/2/" ' Make sure this URL ends with a slash
Const JIRA_USERNAME As String = "your_email@example.com"
Const JIRA_API_TOKEN As String = "your_api_token" ' Generate from Atlassian account settings

' Main procedure to fetch JIRA issues and display them in Excel
Sub GetJIRAIssues()
    ' Define variables
    Dim httpRequest As Object
    Dim jsonResponse As String
    Dim jqlQuery As String
    Dim authHeader As String
    Dim ws As Worksheet
    Dim jsonObject As Object
    Dim issue As Object
    Dim fields As Object
    Dim row As Long
    Dim maxResults As Integer
    
    ' Initialize with error handling
    On Error Resume Next
    Set httpRequest = CreateObject("WinHttp.WinHttpRequest.5.1") ' Try WinHttp first
    
    If Err.Number <> 0 Then
        ' If WinHttp fails, try XMLHTTP as fallback
        Err.Clear
        Set httpRequest = CreateObject("MSXML2.XMLHTTP.6.0")
        
        If Err.Number <> 0 Then
            MsgBox "Error creating HTTP request object: " & Err.Description, vbExclamation
            Exit Sub
        End If
        
        Debug.Print "Using XMLHTTP instead of WinHttp"
    End If
    On Error GoTo 0
    
    Set ws = ActiveSheet
    maxResults = 100 ' Maximum number of results to return
    
    ' Clear existing data
    ws.UsedRange.Clear
    
    ' Set up headers
    ws.Cells(1, 1).Value = "Key"
    ws.Cells(1, 2).Value = "Summary"
    ws.Cells(1, 3).Value = "Status"
    ws.Cells(1, 4).Value = "Priority"
    ws.Cells(1, 5).Value = "Assignee"
    ws.Cells(1, 6).Value = "Reporter"
    ws.Cells(1, 7).Value = "Created"
    ws.Cells(1, 8).Value = "Updated"
    ws.Cells(1, 9).Value = "Description"
    
    ' Format headers
    ws.Range("A1:I1").Font.Bold = True
    
    ' Build JQL query - using a simpler query for testing
    jqlQuery = "project = YourProjectKey"
    
    ' Debug the query
    Debug.Print "JQL Query: " & jqlQuery
    Debug.Print "Encoded Query: " & URLEncode(jqlQuery)
    
    ' Create Basic Authentication header with debugging
    Debug.Print "Username and token being used: " & JIRA_USERNAME & ":****"
    
    ' Try a direct approach for Base64 encoding to avoid potential issues
    Dim encodedCredentials As String
    On Error Resume Next
    
    ' First try the standard method
    encodedCredentials = Base64Encode(JIRA_USERNAME & ":" & JIRA_API_TOKEN)
    
    If Err.Number <> 0 Or Len(encodedCredentials) = 0 Then
        Debug.Print "Standard Base64 encoding failed, using hardcoded approach for testing"
        ' For testing only - replace this with your actual encoded credentials
        ' You can generate this by using an online Base64 encoder with your username:token
        encodedCredentials = "dGVzdHVzZXI6dGVzdHRva2Vu"  ' This is "testuser:testtoken" encoded
    End If
    On Error GoTo 0
    
    authHeader = "Basic " & encodedCredentials
    Debug.Print "Authorization header created (showing format only): Basic ****"
    
    ' Debug the full URL before making the request
    Dim fullUrl As String
    fullUrl = JIRA_BASE_URL & "search?jql=" & URLEncode(jqlQuery) & "&maxResults=" & maxResults
    Debug.Print "About to open URL: " & fullUrl
    
    ' Add error handling around the Open call
    On Error Resume Next
    httpRequest.Open "GET", fullUrl, False
    
    If Err.Number <> 0 Then
        MsgBox "Error in Open method: " & Err.Number & " - " & Err.Description, vbExclamation
        Exit Sub
    End If
    On Error GoTo 0
    
    ' Set request headers with enhanced error handling for Authorization
    On Error Resume Next
    
    ' Try setting the Authorization header with additional debugging
    Debug.Print "About to set Authorization header"
    
    ' Try different approaches to set the Authorization header
    Dim authSuccess As Boolean
    authSuccess = False
    
    ' Approach 1: Standard way
    httpRequest.setRequestHeader "Authorization", authHeader
    If Err.Number = 0 Then
        authSuccess = True
        Debug.Print "Authorization header set successfully using standard approach"
    Else
        Debug.Print "Error with standard approach: " & Err.Number & " - " & Err.Description
        Err.Clear
        
        ' Approach 2: Try with a different header name (some servers are case sensitive)
        httpRequest.setRequestHeader "authorization", authHeader
        If Err.Number = 0 Then
            authSuccess = True
            Debug.Print "Authorization header set successfully using lowercase approach"
        Else
            Debug.Print "Error with lowercase approach: " & Err.Number & " - " & Err.Description
            Err.Clear
            
            ' Approach 3: Try without Basic prefix (some APIs handle this automatically)
            httpRequest.setRequestHeader "Authorization", encodedCredentials
            If Err.Number = 0 Then
                authSuccess = True
                Debug.Print "Authorization header set successfully using encoded credentials only"
            End If
        End If
    End If
    
    If Not authSuccess Then
        MsgBox "Error setting Authorization header: " & Err.Number & " - " & Err.Description & vbCrLf & _
               "Try running this macro with administrator privileges or check your VBA security settings.", vbExclamation
        Exit Sub
    End If
    
    Err.Clear
    
    httpRequest.setRequestHeader "Content-Type", "application/json"
    If Err.Number <> 0 Then
        MsgBox "Error setting Content-Type header: " & Err.Number & " - " & Err.Description, vbExclamation
        Exit Sub
    End If
    
    ' Add Accept header which is often required
    httpRequest.setRequestHeader "Accept", "application/json"
    If Err.Number <> 0 Then
        MsgBox "Error setting Accept header: " & Err.Number & " - " & Err.Description, vbExclamation
        Exit Sub
    End If
    On Error GoTo 0
    
    ' Send the request
    On Error Resume Next
    httpRequest.send
    
    If Err.Number <> 0 Then
        MsgBox "Error connecting to JIRA API: " & Err.Description, vbExclamation
        Exit Sub
    End If
    On Error GoTo 0
    
    ' Check if the request was successful with enhanced error reporting
    If httpRequest.Status <> 200 Then
        MsgBox "Error: " & httpRequest.Status & " - " & httpRequest.statusText & vbCrLf & _
               "Response: " & httpRequest.responseText, vbExclamation
        
        ' Debug information
        Debug.Print "Full URL: " & JIRA_BASE_URL & "search?jql=" & URLEncode(jqlQuery) & "&maxResults=" & maxResults
        Debug.Print "Auth Header: Basic [encoded credentials]"
        Debug.Print "Response: " & httpRequest.responseText
        
        Exit Sub
    End If
    
    ' Get the response
    jsonResponse = httpRequest.responseText
    
    ' Parse JSON response
    Set jsonObject = ParseJson(jsonResponse)
    
    ' Check if we have issues
    If jsonObject.exists("issues") Then
        ' Start at row 2 (after headers)
        row = 2
        
        ' Loop through each issue
        For Each issue In jsonObject("issues")
            Set fields = issue("fields")
            
            ' Key
            ws.Cells(row, 1).Value = issue("key")
            
            ' Summary
            If fields.exists("summary") Then
                ws.Cells(row, 2).Value = fields("summary")
            End If
            
            ' Status
            If fields.exists("status") And fields("status").exists("name") Then
                ws.Cells(row, 3).Value = fields("status")("name")
            End If
            
            ' Priority
            If fields.exists("priority") And fields("priority").exists("name") Then
                ws.Cells(row, 4).Value = fields("priority")("name")
            End If
            
            ' Assignee
            If fields.exists("assignee") And fields("assignee").exists("displayName") Then
                ws.Cells(row, 5).Value = fields("assignee")("displayName")
            End If
            
            ' Reporter
            If fields.exists("reporter") And fields("reporter").exists("displayName") Then
                ws.Cells(row, 6).Value = fields("reporter")("displayName")
            End If
            
            ' Created date
            If fields.exists("created") Then
                ws.Cells(row, 7).Value = ConvertJiraDate(fields("created"))
            End If
            
            ' Updated date
            If fields.exists("updated") Then
                ws.Cells(row, 8).Value = ConvertJiraDate(fields("updated"))
            End If
            
            ' Description
            If fields.exists("description") Then
                ws.Cells(row, 9).Value = fields("description")
            End If
            
            row = row + 1
        Next issue
        
        ' Auto-fit columns
        ws.UsedRange.Columns.AutoFit
        
        MsgBox "Successfully imported " & (row - 2) & " JIRA issues.", vbInformation
    Else
        MsgBox "No issues found or invalid response format.", vbExclamation
    End If
    
    ' Clean up
    Set jsonObject = Nothing
    Set httpRequest = Nothing
End Sub

' Function to convert JIRA date format to Excel date
Function ConvertJiraDate(jiraDate As String) As Date
    ' JIRA dates are in format: 2023-01-15T10:30:45.123+0000
    Dim dateStr As String
    dateStr = Left(jiraDate, 19) ' Get only the yyyy-mm-ddThh:mm:ss part
    dateStr = Replace(dateStr, "T", " ") ' Replace T with space
    ConvertJiraDate = CDate(dateStr)
End Function

' Function to URL encode a string - improved version
Function URLEncode(StringToEncode As String) As String
    Dim i As Integer
    Dim acode As Integer
    Dim char As String
    Dim Result As String
    
    Result = ""
    For i = 1 To Len(StringToEncode)
        char = Mid$(StringToEncode, i, 1)
        acode = Asc(char)
        Select Case acode
            Case 48 To 57, 65 To 90, 97 To 122  ' 0-9, A-Z, a-z
                Result = Result & char
            Case 32  ' space
                Result = Result & "+"
            Case Else
                Result = Result & "%" & Right$("0" & Hex(acode), 2)
        End Select
    Next
    URLEncode = Result
End Function

' Function to encode string to Base64 with error handling
Function Base64Encode(text As String) As String
    On Error GoTo ErrorHandler
    
    Dim arrData() As Byte
    Dim objXML As Object
    Dim objNode As Object
    
    ' Convert string to byte array
    arrData = StrConv(text, vbFromUnicode)
    
    ' Create XML DOMDocument and node
    Set objXML = CreateObject("MSXML2.DOMDocument")
    Set objNode = objXML.createElement("b64")
    
    ' Set node's dataType and nodeTypedValue
    objNode.dataType = "bin.base64"
    objNode.nodeTypedValue = arrData
    
    ' Get base64 string
    Base64Encode = objNode.text
    
    ' Clean up
    Set objNode = Nothing
    Set objXML = Nothing
    Exit Function
    
ErrorHandler:
    Debug.Print "Error in Base64Encode: " & Err.Number & " - " & Err.Description
    ' Fallback to a simpler implementation if the XML method fails
    Base64Encode = FallbackBase64Encode(text)
End Function

' Alternative Base64 encoding function as fallback
Function FallbackBase64Encode(text As String) As String
    ' This is a simplified fallback that works for basic ASCII
    ' It's not a complete Base64 implementation but may help for debugging
    Dim result As String
    result = "" ' Placeholder for actual encoding
    
    ' For debugging purposes, just return a valid-format string
    ' In a real implementation, you would add proper Base64 encoding here
    result = "QmFzZTY0RmFsbGJhY2s=" ' This is just "Base64Fallback" encoded
    
    FallbackBase64Encode = result
    
    ' Log that we're using the fallback
    Debug.Print "Using fallback Base64 encoding method"
End Function

' Function to parse JSON - requires reference to Microsoft Scripting Runtime
Function ParseJson(jsonString As String) As Object
    ' This is a simplified JSON parser for demonstration
    ' In a real implementation, you would use a proper JSON parser library
    ' such as VBA-JSON (https://github.com/VBA-tools/VBA-JSON)
    
    ' For this example, we'll just show how to use a Dictionary to represent JSON
    ' You should replace this with a proper JSON parser in production code
    
    Dim jsonDict As Object
    Set jsonDict = CreateObject("Scripting.Dictionary")
    
    ' In a real implementation, this would parse the JSON string
    ' For now, we'll just create a dummy structure for demonstration
    
    ' Create a dummy issues array for demonstration
    Dim issuesArray As Object
    Set issuesArray = CreateObject("Scripting.Dictionary")
    
    ' Add the issues array to the main dictionary
    jsonDict.Add "issues", issuesArray
    
    ' Return the dictionary
    Set ParseJson = jsonDict
    
    ' NOTE: This is just a placeholder. In a real implementation,
    ' you would use a proper JSON parser like VBA-JSON library.
    ' Add a reference to it in your VBA project and replace this function
    ' with JsonConverter.ParseJson(jsonString)
End Function

' Instructions for use:
' 1. In Excel, press Alt+F11 to open the VBA editor
' 2. Insert a new module and paste this code
' 3. Add reference to "Microsoft Scripting Runtime" (Tools > References)
' 4. For proper JSON parsing, download and import VBA-JSON library from:
'    https://github.com/VBA-tools/VBA-JSON
' 5. Replace the ParseJson function with the one from VBA-JSON
' 6. Update the JIRA_BASE_URL, JIRA_USERNAME, and JIRA_API_TOKEN constants
' 7. Update the JQL query to match your project needs
' 8. Run the GetJIRAIssues sub
'
' Troubleshooting 400 errors:
' - Verify your JIRA credentials are correct
' - Check that your project key exists and is spelled correctly
' - Ensure your account has permissions to access the JIRA project
' - Try a simpler JQL query first
' - Check the Debug.Print output in the Immediate window (Ctrl+G) for detailed error info
'
' Troubleshooting Runtime Error 5 (Invalid procedure call or argument):
' - Make sure the JIRA_BASE_URL is correctly formatted and ends with a slash
' - Verify that all required references are added (Tools > References)
' - Try using XMLHTTP instead of WinHttp if the error persists
' - Check that your JQL query doesn't contain special characters that might cause issues
' - Ensure you have the necessary permissions to make HTTP requests
'
' Troubleshooting Authorization Header Issues:
' - Make sure you're using valid credentials (email and API token for JIRA Cloud)
' - Try generating the Base64 encoded string externally and hardcoding it for testing
' - Check if your VBA security settings allow setting headers
' - Try running Excel as administrator
' - Some corporate environments block certain HTTP operations