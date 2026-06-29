# topK vs Threshold Tuning - Quick Reference

## What Changed

Your vector search now supports **two tuning parameters**:

### 1. **topK** - Maximum Results to Return
- Controls how many results are returned (0-N)
- Applied AFTER filtering by threshold
- Default: 5
- Use case: "I want to see up to 5 most similar results"

### 2. **threshold** - Similarity Cutoff
- Maximum distance (L2) to include results
- Applied FIRST before topK limit
- Default: 0.5
- Range: 0.0 (identical) to 2.0 (different)
- Use case: "I only want results that are at least 70% similar (threshold=0.3)"

## How They Work Together

```
Database → Filter by threshold → Sort by distance → Limit to topK → Return results
           (keeps only similar)    (best first)      (final count)
```

## Quick Tuning Examples

| Goal | topK | Threshold | Reasoning |
|------|------|-----------|-----------|
| Exact matches only | 3 | 0.2 | Strict filter, small result set |
| High quality, few results | 5 | 0.3 | Strict threshold |
| Balanced (default) | 5 | 0.5 | Good for most cases |
| More options | 10 | 0.5 | Larger result set |
| Broad exploration | 10 | 1.0 | Show all related items |
| Just get N results | 5 | 2.0 | Ignore quality, get exactly 5 |

## API Usage

### Minimum (Uses Defaults)
```json
{ "purpose": "search text" }
```

### Custom Tuning
```json
{
  "purpose": "search text",
  "topK": 3,
  "threshold": 0.2
}
```

## Key Insights

1. **Lower threshold = Stricter matching** (fewer, better results)
2. **Higher topK = More results** (could include lower quality)
3. **threshold applies first** - If threshold=0.2, only items with distance ≤ 0.2 considered
4. **topK applies second** - Then return up to topK items
5. **Distance 0.0 = Perfect match**, Higher = Less similar

## Distance Scale Cheat Sheet

```
0.0         Very Identical (same meaning)
│
0.1-0.2     Similar (closely related)
│
0.3-0.5     Related (same domain, different aspects)
│
0.6-0.9     Loosely related (some connection)
│
1.0+        Different (separate concepts)
│
2.0         Maximum distance (normalized vectors)
```

## Files Modified/Created

1. **CompanyService.kt** - Added topK and threshold parameters
2. **CompanyController.kt** - Updated endpoint to accept parameters
3. **PurposeRequest.kt** - Added topK and threshold fields
4. **TUNING_GUIDE.md** - Detailed tuning guide
5. **API_TESTING.md** - API testing examples

## Next Steps

1. **Test with your data**: Try different topK/threshold values
2. **Monitor results**: Check distance distributions
3. **Iterate**: Adjust parameters based on results
4. **Document**: Record which settings work best for your use cases

## Common Issues & Fixes

### Problem: Getting 0 results
- **Solution**: Increase threshold (try 0.5 → 0.7 → 1.0)
- **Reason**: All results exceed distance threshold

### Problem: Too many low-quality results
- **Solution**: Decrease threshold (try 0.5 → 0.3 → 0.2)
- **Reason**: Including too many dissimilar items

### Problem: Always getting exactly topK results
- **Solution**: Threshold might be too high (set to 2.0 to remove filter)
- **Reason**: All items pass threshold filter

### Problem: Performance is slow
- **Solution**: Decrease topK, keep strict threshold
- **Reason**: Tighter threshold may use better indexes

## Performance Metrics to Track

```sql
-- Check distance distribution of your data
SELECT 
  embedding <-> '[query_vector]'::vector as distance,
  COUNT(*) as count
FROM purposes
GROUP BY embedding <-> '[query_vector]'::vector
ORDER BY distance;
```

This helps you understand:
- What distance values are typical
- Where to set threshold for optimal results
- How many results you'll usually get
