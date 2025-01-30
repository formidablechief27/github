# Install necessary libraries
# pip install requests beautifulsoup4

import requests
from bs4 import BeautifulSoup

# URL of the webpage you want to scrape
url = 'https://codeforces.com/contest/2026/submission/288622520'

# Send a GET request to fetch the page content
response = requests.get(url)

# Check if the request was successful
if response.status_code == 200:
    # Parse the page content
    soup = BeautifulSoup(response.text, 'html.parser')
    
    # Find all <pre> tags
    pre_tags = soup.find_all('pre')
    
    # Print the content inside each <pre> tag
    for idx, pre in enumerate(pre_tags, start=1):
        print(f"<pre> tag {idx} content:")
        print(pre.get_text())  # Use get_text() to get the text content inside <pre> tags
        print("="*50)  # Separator for readability
        break
else:
    print(f"Failed to retrieve the page. Status code: {response.status_code}")
