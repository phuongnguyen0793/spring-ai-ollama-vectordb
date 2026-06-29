# Vector Search Tuning Guide: topK vs Threshold

## Overview
When searching for similar purposes using vector embeddings, two key parameters control the search results:
- **topK**: Maximum number of results to return
- **threshold**: Maximum distance to include results (similarity cutoff)

## Distance Metric (L2 Euclidean)
The `<->` operator in pgvector returns **L2 (Euclidean) distance**:
- **0.0** = Identical vectors (perfect match)
- **0.1-0.3** = Very similar (almost identical meaning)
- **0.3-0.6** = Similar (related topics)
- **0.6-1.0** = Somewhat similar (loose connection)
- **1.0+** = Different vectors (different meanings)
- **2.0** = Maximum L2 distance for normalized vectors

## Usage Examples

### 1. Conservative Search (Strict Similarity)
```kotlin
searchSimilarPurposes(
    purposeText = "software development company",
    topK = 3,           // Only get top 3 most similar
    threshold = 0.2     // Must be very similar
)
```
**When to use**: When you need only high-quality, highly relevant matches

---

### 2. Moderate Search (Balanced)
```kotlin
searchSimilarPurposes(
    purposeText = "software development company",
    topK = 5,           // Get up to 5 results
    threshold = 0.5     // Moderate similarity threshold
)
```
**When to use**: Default use case, good balance between recall and precision

---

### 3. Broad Search (High Recall)
```kotlin
searchSimilarPurposes(
    purposeText = "software development company",
    topK = 10,          // Get more results
    threshold = 1.0     // More lenient threshold
)
```
**When to use**: When you want more options, even if loosely related

---

### 4. Only topK (No Threshold Filter)
```kotlin
searchSimilarPurposes(
    purposeText = "software development company",
    topK = 5,
    threshold = 2.0     // Accept all distances (no effective cutoff)
)
```
**When to use**: Always get exactly topK results, regardless of quality

---

## Tuning Strategy

### Step 1: Start with Defaults
```kotlin
topK = 5
threshold = 0.5
```

### Step 2: Test and Observe Results
```sql
-- Manually test searches to understand distance distribution
SELECT id, purpose_text, embedding <-> '[0.1, 0.2, 0.3, ...]'::vector as distance
FROM purposes
ORDER BY distance ASC
LIMIT 20;
```

### Step 3: Adjust Based on Needs

**If getting too few results:**
- Increase `threshold` (0.5 → 0.7 → 1.0)
- Increase `topK` (5 → 10)

**If getting too many irrelevant results:**
- Decrease `threshold` (0.5 → 0.3 → 0.2)
- Decrease `topK` (10 → 5 → 3)

**If getting perfect results but want to limit cost:**
- Keep `threshold` the same
- Decrease `topK` to reduce computation

## Performance Considerations

### Memory & Speed
- Smaller `topK` = Faster queries, less memory
- Stricter `threshold` = More indexed queries, potentially faster

### Example Tuning Matrix

| Use Case | topK | Threshold | Result Count | Quality | Notes |
|----------|------|-----------|--------------|---------|-------|
| Exact matches | 3 | 0.1 | 0-3 | Very High | Strict, fast |
| High precision | 5 | 0.3 | 1-5 | High | Balance |
| Standard | 5 | 0.5 | 1-5 | Medium | Default |
| High recall | 10 | 0.7 | 5-10 | Lower | Broad |
| Exploratory | 20 | 1.5 | 10-20 | Mixed | Very broad |

## Real-World Examples

### E-commerce Product Recommendation
```kotlin
searchSimilarPurposes(topK = 5, threshold = 0.4)  // Related but not identical
```

### Customer Support Ticket Matching
```kotlin
searchSimilarPurposes(topK = 3, threshold = 0.2)  // Must be very similar
```

### Content Discovery
```kotlin
searchSimilarPurposes(topK = 10, threshold = 0.8)  // Show diverse but related
```

## API Usage

### Default (Recommended for Most Cases)
```kotlin
val results = companyService.searchSimilarPurposes("software company")
// topK = 5, threshold = 0.5
```

### Custom Parameters
```kotlin
val results = companyService.searchSimilarPurposes(
    purposeText = "software company",
    topK = 10,
    threshold = 0.3
)
```

## Monitoring & Metrics

Track these metrics to find optimal parameters:
- **Average distance** of returned results
- **Result count variance** (how often you hit topK limit)
- **User satisfaction** with result relevance
- **Query latency** (distance threshold affects index usage)

## Gotchas & Tips

1. **Distance is NOT similarity**: Lower distance = More similar
2. **Threshold applies FIRST**: Only results ≤ threshold are considered
3. **topK is applied SECOND**: Then limited to topK results
4. **Always test with real data**: Distance distributions vary by domain
5. **Monitor embedding quality**: Poor embeddings → high distances everywhere
