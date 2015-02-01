#Business Model of Travel Product

**TravelProduct**
A travel product is a product including instructions, agreements, restrictions, travel items and all others illurstrations of a travel.

A travel product should exclude all ambiguties and be actionable for enrollment and payment after it was finalized and published.

**HotelItem**
A hotel item is a travel item defining all the details about the hotel and preserve information in the travel product.

The hotel items in a product are immutable if the product bidding is still valid.

**TrafficItem**
A traffic item is a traffic item defining all the details about the traffic and preserve information in the travel product.

The traffic items in a product are immutable if the product bidding is still valid.

**ResortItem**
A resort item is a resort item defining all the details about the resort and preserve information in the travel product.

The resort items in a product are immutable if the product bidding is still valid.

**TrivItem**
A triv item in a trivial item defining optional product items for the product.

The trivial items are subject to change and optional to be included in a product order. The product bidding does not cover trivial items.

**Bidding**
A bidding is the bidding of the associated product.

###Travel Product and Product Items
> * A travel product typically includes multiple travel items in different types.
> * Except trivial items, a travel product and the contained items are immutable once it was published and opened for enrollment and payment.
> * A travel product contains only one bidding.
> * The trivial items can only be added once the product was published and opened for enrollment and payment.


### TravelProduct

```json
        {
           "uuid": "b4075912-898d-48ed-a824-1ee77b06e258",
           "ref_uuid": "930096be-c6f4-4385-8327-8ca9ca4e3d93",
           "items":
           [
               {
                   "uuid": "c9d61478-95f8-4839-8062-140185b9b1a6",
                   "sub_type": "hotel",
                   "arrival_datetime": 1423575774113,
                   "departure_datetime": 1423575774113,
                   "hotel":
                   {
                   }
               },
               {
                   "uuid": "03d9cb02-5678-42b9-9196-b2738848758e",
                   "sub_type": "traffic",
                   "date": 1422452574113,
                   "traffic":
                   {
                       "traffice_tool_type": 1
                   }
               },
               {
                   "uuid": "987ee9f1-c12b-4321-9e35-d4359d975a27",
                   "sub_type": "resort",
                   "date": 1422538974113,
                   "resort":
                   {
                   }
               },
               {
                   "uuid": "66d01714-042e-470e-94e1-04409b516042",
                   "sub_type": "triv"
               },
               {
                   "bidding":
                   [
                       {
                           "min": 1,
                           "max": 5,
                           "price": 9999
                       },
                       {
                           "min": 6,
                           "max": 2147483647,
                           "price": 8888
                       }
                   ]
               }
           ],
           "group_capacity": 68,
           "deadline_datetime": 1422625374113,
           "departure_datetime": 1422798174113
       }
```

### TravelProductItem

```json
        {
           "uuid": "c9d61478-95f8-4839-8062-140185b9b1a6",
           "sub_type": "hotel",
           "arrival_datetime": 1423575774113,
           "departure_datetime": 1423575774113,
           "hotel":
           {
           }
       },
       {
           "uuid": "03d9cb02-5678-42b9-9196-b2738848758e",
           "sub_type": "traffic",
           "date": 1422452574113,
           "traffic":
           {
               "traffice_tool_type": 1
           }
       },
       {
           "uuid": "987ee9f1-c12b-4321-9e35-d4359d975a27",
           "sub_type": "resort",
           "date": 1422538974113,
           "resort":
           {
           }
       },
       {
           "uuid": "66d01714-042e-470e-94e1-04409b516042",
           "sub_type": "triv"
       }
```

### Bidding

```json
        {
           "bidding":
           [
               {
                   "min": 1,
                   "max": 5,
                   "price": 9999
               },
               {
                   "min": 6,
                   "max": 2147483647,
                   "price": 8888
               }
           ]
       }
```
