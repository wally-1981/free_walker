#Business Model of Travel Proposal

**TravelProposal**
A travel proposal is typically submitted by tourists to describe a set of travel requirements, including itinerary, hotel, traffic, resort, etc.

A TravelProposal usually contains ambiguties and not actionable before it evolves into a travel product.

**ItineraryRequirement**
An itinerary requirement is part of a travel proposal, describe the travel itinerary from a source location A to a destination location B and when.

**HotelRequirement**
A hotel requirement is part of a travel proposal and relating with an itinerary requirement, describe requirements about hotel during the related itinerary.

**TrafficRequirement**
A traffic requirement is part of a travel proposal and relating with an itinerary requirement, describe requirements about traffic during the related itinerary.

**ResortRequirement**
A resort requirement is part of a travel proposal and relating with an itinerary requirement, describe requirements about resort during the related itinerary.

###Travel Proposal and Travel Requirement
> * One TravelProposal contains one or more travel requirements.
> * All travel requirements are categorized into specific requirement by types, such as Itinerary, Hotel, Traffic and Resort.
> * One or more ItineraryRequirement contained by TravelProposal are order sensitive, and the order implies the sequence of travel itineraries.
> * Every non-itinerary travel requirement belongs to specific ItineraryRequirement.

### TravelProposal

```json
{
           "uuid": "7b8faf69-2086-4c05-9d2c-6d8d8671e429",
           "type": "proposal",
           "author": "3b3e4dcf-e353-4418-adfb-3c9af7a54992",
           "title": "台北到巴萨看梅西",
           "requirements":
           [
               {
                   "uuid": "b5ab9059-31c9-44eb-b4be-ca512731833a",
                   "type": "itinerary",
                   "destination":
                   {
                       "city":
                       {
                           "uuid":
                           [
                               "02515d41-f141-4175-9a11-9e68b9cfe687",
                               "6e0bcba8-b19f-4ce9-8960-18e3e2f607f6",
                               "af70a55c-eb4c-415c-8375-88081716f8b8",
                               "1"
                           ],
                           "name": "Taibei",
                           "chinese_name": "台北",
                           "pinyin_name": "taibei"
                       }
                   },
                   "departure":
                   {
                       "city":
                       {
                           "uuid":
                           [
                               "84844276-3036-47dd-90e0-f095cfa98da5",
                               "00000000-0000-0000-0000-000000000000",
                               "fe80acb2-816e-45f0-a988-19caa587c6fc",
                               "2"
                           ],
                           "name": "Barcelona",
                           "chinese_name": "巴塞罗纳",
                           "pinyin_name": "basailuona"
                       }
                   }
               }
           ],
           "tags":
           [
           ]
       }
```

### TravelRequirement

```json
{
           "uuid": "edb00013-dcb9-483b-b140-c4f1d73cb5df",
           "type": "requirement",
           "sub_type": "hotel",
           "night": 6
       },
       {
           "uuid": "5de9ca31-a6a7-4d22-b341-ee4ad72c8c02",
           "type": "requirement",
           "sub_type": "hotel",
           "night": 6,
           "star": 500
       },
       {
           "uuid": "841b4664-e852-48ca-be40-29e52fad6583",
           "type": "requirement",
           "sub_type": "hotel",
           "night": 6,
           "hotel":
           {
           },
           "arrival_datetime": 1422366174097
       },
       {
           "uuid": "52375e45-95a8-469e-917d-bad01a2c61b6",
           "type": "requirement",
           "sub_type": "resort",
           "time_range_start": 12,
           "time_range_offset": 6
       },
       {
           "uuid": "819cd1dc-3d68-42fd-9cf0-3a314b406d44",
           "type": "requirement",
           "sub_type": "resort",
           "time_range_start": 12,
           "time_range_offset": 6,
           "star": 200
       },
       {
           "uuid": "b4d30498-be55-4eae-b465-2cf94ba9a18d",
           "type": "requirement",
           "sub_type": "resort",
           "time_range_start": 12,
           "time_range_offset": 6,
           "resort":
           {
           }
       },
       {
           "uuid": "3c6b06db-ee02-47f1-b65f-216f2612ec8a",
           "type": "requirement",
           "sub_type": "traffic",
           "traffice_tool_type": 2
       },
       {
           "uuid": "2a1caba7-c55f-4a46-b2b0-ac4283c5a5cc",
           "type": "requirement",
           "sub_type": "traffic",
           "traffice_tool_type": 1,
           "datetime_range_selections":
           [
               {
                   "time_range_start": 18,
                   "time_range_offset": 6
               }
           ]
       },
       {
           "uuid": "5365aa91-3af3-4204-9ab3-40195960ff48",
           "type": "requirement",
           "sub_type": "traffic",
           "traffice_tool_type": 1,
           "datetime_range_selections":
           [
               {
                   "time_range_start": 0,
                   "time_range_offset": 6
               },
               {
                   "time_range_start": 18,
                   "time_range_offset": 6
               }
           ]
       },
       {
           "uuid": "4b15c0bc-ecef-473e-a8e1-ce628b3454ad",
           "type": "requirement",
           "sub_type": "traffic",
           "traffice_tool_type": 1,
           "flight":
           {
               "traffice_tool_type": 1
           }
       },
       {
           "uuid": "db7c7786-0a8b-414a-9b09-9a6616f38f3e",
           "type": "requirement",
           "sub_type": "traffic",
           "traffice_tool_type": 2,
           "train":
           {
               "traffice_tool_type": 2
           }
       }
```
