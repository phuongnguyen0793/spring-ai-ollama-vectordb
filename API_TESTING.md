# Vector Search API - Testing Guide

## Endpoint: POST /api/search-purpose

### Request Format
```json
{
  "purpose": "software development company",
  "topK": 5,         // Optional, default: 5
  "threshold": 0.5   // Optional, default: 0.5
}
```

### Response Format
```json
{
  "query": "software development company",
  "topK": 5,
  "threshold": 0.5,
  "count": 3,
  "results": [
    {
      "id": 1,
      "purposeText": "...",
      "distance": 0.15
    },
    {
      "id": 2,
      "purposeText": "...",
      "distance": 0.28
    },
    {
      "id": 3,
      "purposeText": "...",
      "distance": 0.45
    }
  ]
}
```

## Test Cases

### 1. Default Search (Balanced)
```bash
curl -X POST http://localhost:8080/api/search-purpose \
  -H "Content-Type: application/json" \
  -d '{
    "purpose": "software development"
  }'
```

### 2. Strict Similarity Search
```bash
curl -X POST http://localhost:8080/api/search-purpose \
  -H "Content-Type: application/json" \
  -d '{
    "purpose": "software development",
    "topK": 3,
    "threshold": 0.2
  }'
```

### 3. Broad Search
```bash
curl -X POST http://localhost:8080/api/search-purpose \
  -H "Content-Type: application/json" \
  -d '{
    "purpose": "software development",
    "topK": 10,
    "threshold": 1.0
  }'
```

### 4. Only Top K
```bash
curl -X POST http://localhost:8080/api/search-purpose \
  -H "Content-Type: application/json" \
  -d '{
    "purpose": "software development",
    "topK": 5,
    "threshold": 2.0
  }'
```

## Distance Interpretation

| Distance | Similarity | Interpretation |
|----------|-----------|-----------------|
| 0.0 - 0.1 | Identical | Same or nearly identical meaning |
| 0.1 - 0.3 | Very Similar | Closely related, almost same topic |
| 0.3 - 0.5 | Similar | Related topics, similar intent |
| 0.5 - 0.7 | Loosely Related | Some connection, but different focus |
| 0.7 - 1.0 | Distant | Weakly related, different domains |
| 1.0+ | Very Different | Unrelated concepts |

## Tuning Recommendations

### For Product Search
- **topK**: 5-10
- **threshold**: 0.3-0.4
- **Goal**: Show highly relevant alternatives

### For Customer Support Tickets
- **topK**: 3-5
- **threshold**: 0.2-0.3
- **Goal**: Find exact matches only

### For Content Discovery
- **topK**: 10-20
- **threshold**: 0.7-1.0
- **Goal**: Show diverse but related content

### For Analytics
- **topK**: Unlimited
- **threshold**: High (>1.0)
- **Goal**: See full distribution of similarities

## Performance Tips

1. **Start conservative**: Use threshold=0.5, topK=5
2. **Monitor distance distribution**: Check actual distance values
3. **Adjust incrementally**: Change one parameter at a time
4. **Test with real data**: Use actual queries from your domain
5. **Cache embeddings**: Avoid recomputing same searches

## Example: Finding Similar Company Purposes

1. Start with default:
```json
{
  "purpose": "We develop financial software solutions",
  "topK": 5,
  "threshold": 0.5
}
```

2. If too few results, increase threshold:
```json
{
  "purpose": "We develop financial software solutions",
  "topK": 5,
  "threshold": 0.7
}
```

3. If results are too broad, decrease threshold:
```json
{
  "purpose": "We develop financial software solutions",
  "topK": 5,
  "threshold": 0.3
}
```

4. Fine-tune topK based on results needed:
```json
{
  "purpose": "We develop financial software solutions",
  "topK": 10,
  "threshold": 0.3
}
```
