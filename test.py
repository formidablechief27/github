import os
import re
from datetime import datetime
import time
import pandas as pd
import matplotlib.pyplot as plt
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from dotenv import load_dotenv

# Configuration
load_dotenv()
API_KEY = 'api-key'
QUERIES = ["women safety", "#womensafety", "gender violence", "sexual harassment prevention"]
MAX_RESULTS = 200  # Per query
RESULTS_PER_PAGE = 200

# Initialize YouTube API
youtube = build('youtube', 'v3', developerKey=API_KEY)

def safe_request(request):
    try:
        return request.execute()
    except HttpError as e:
        if e.resp.status == 403:
            print("API quota exceeded. Waiting 1 hour...")
            time.sleep(3600)
            return safe_request(request)
        raise

def fetch_videos():
    all_videos = []
    
    for query in QUERIES:
        next_page_token = None
        results_collected = 0
        
        while results_collected < MAX_RESULTS:
            try:
                request = youtube.search().list(
                    q=query,
                    part="id,snippet",
                    maxResults=min(RESULTS_PER_PAGE, MAX_RESULTS - results_collected),
                    type="video",
                    pageToken=next_page_token,
                    order="date",
                    relevanceLanguage="en"
                )
                response = safe_request(request)
                
                for item in response['items']:
                    video_data = {
                        'video_id': item['id']['videoId'],
                        'title': item['snippet']['title'],
                        'published_at': item['snippet']['publishedAt'],
                        'channel_id': item['snippet']['channelId'],
                        'query': query
                    }
                    all_videos.append(video_data)
                
                results_collected += len(response['items'])
                next_page_token = response.get('nextPageToken')
                if not next_page_token:
                    break
                                
            except Exception as e:
                print(f"Error fetching data: {str(e)}")
                break

    return pd.DataFrame(all_videos).drop_duplicates('video_id')

def enrich_with_location(df):
    locations = []
    for channel_id in df['channel_id'].unique():
        try:
            request = youtube.channels().list(
                part="snippet,statistics",
                id=channel_id
            )
            response = safe_request(request)
            if response['items']:
                loc = response['items'][0]['snippet'].get('country', 'Unknown')
                locations.append({'channel_id': channel_id, 'country': loc})
        except Exception as e:
            print(f"Error fetching channel {channel_id}: {str(e)}")
    
    return df.merge(pd.DataFrame(locations), on='channel_id', how='left')

def analyze_data(df):
    df['published_at'] = pd.to_datetime(df['published_at'])
    df['month_year'] = df['published_at'].dt.to_period('M')
    
    monthly_stats = df.groupby('month_year').agg(
        total_videos=('video_id', 'count'),
        unique_channels=('channel_id', 'nunique')
    ).reset_index()
    
    country_stats = df.groupby('country').agg(
        video_count=('video_id', 'count'),
        top_channel=('channel_id', lambda x: x.value_counts().index[0])
    ).reset_index()
    
    return monthly_stats, country_stats

def visualize_results(monthly_stats, country_stats):
    plt.style.use('ggplot')
    
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(14, 10))
    
    monthly_stats['month_year'] = monthly_stats['month_year'].astype(str)
    ax1.bar(monthly_stats['month_year'], monthly_stats['total_videos'], color='#ff6b6b')
    ax1.set_title('Monthly Video Uploads on Women Safety Topics')
    ax1.set_ylabel('Number of Videos')
    ax1.tick_params(axis='x', rotation=45)
    
    ax2.plot(monthly_stats['month_year'], monthly_stats['unique_channels'], marker='o', color='#4ecdc4')
    ax2.set_title('Unique Channels Posting Content')
    ax2.set_ylabel('Number of Channels')
    ax2.tick_params(axis='x', rotation=45)
    
    plt.tight_layout()
    plt.savefig('temporal_analysis.png')
    
    top_countries = country_stats.nlargest(10, 'video_count')
    plt.figure(figsize=(12, 8))
    plt.barh(top_countries['country'], top_countries['video_count'], color='#45b7d1')
    plt.title('Top 10 Countries by Women Safety Content Production')
    plt.xlabel('Number of Videos')
    plt.gca().invert_yaxis()
    plt.tight_layout()
    plt.savefig('geographical_analysis.png')

def main():
    print("Starting data collection...")
    videos_df = fetch_videos()
    print(f"Collected {len(videos_df)} unique videos")
    
    print("Enriching with location data...")
    enriched_df = enrich_with_location(videos_df)
    
    print("Analyzing data...")
    monthly_stats, country_stats = analyze_data(enriched_df)
    
    print("Generating visualizations...")
    visualize_results(monthly_stats, country_stats)
    
    enriched_df.to_csv('raw_video_data.csv', index=False)
    monthly_stats.to_csv('monthly_stats.csv', index=False)
    country_stats.to_csv('country_stats.csv', index=False)
    print("Results saved to CSV files")
    print("Charts saved as PNG files")

if __name__ == "__main__":
    main()