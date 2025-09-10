# Your First Test - Step by Step Tutorial 👶🧪

## Goal: Write and run your very first test in 10 minutes!

### Before You Start
- ✅ Project is set up (ran setup script)
- ✅ IDE is open with the project
- ✅ You can see the files in the project

### Step 1: Find the Right File (2 minutes)

1. **Open your file explorer** in your IDE
2. **Navigate to**: `src/test/java/com/bikeshare/lab1/`
3. **Find the file**: `BeginnerFirstTest.java`
4. **Double-click** to open it

💡 **Tip**: If you can't find it, use your IDE's search (Ctrl+P or Cmd+P) and type "BeginnerFirstTest"

### Step 2: Understand the File Structure (2 minutes)

Look at the file. You'll see:

```java
@Test
@DisplayName("My very first test - always passes")
void myVeryFirstTest() {
    // This test always passes - it's just to show you the structure
    assertEquals(1, 1);
    
    // Congratulations! You just saw your first passing test! 🎉
}
```

**What each part means:**
- `@Test` = "This is a test method"
- `@DisplayName` = "Human-readable name for the test"
- `void myVeryFirstTest()` = "The actual test method"
- `assertEquals(1, 1)` = "Check that 1 equals 1" (always true!)

### Step 3: Run Your First Test (2 minutes)

**Option A: In VS Code**
1. Click the ▶️ button next to `myVeryFirstTest()`
2. Watch the test run in the terminal

**Option B: In IntelliJ IDEA**
1. Right-click on `myVeryFirstTest()`
2. Choose "Run 'myVeryFirstTest()'"
3. Watch the test run in the bottom panel

**Option C: Command Line**
```bash
mvn test -Dtest=BeginnerFirstTest#myVeryFirstTest
```

**What success looks like:**
- ✅ Green checkmark or "BUILD SUCCESS"
- ✅ "1 test passed" or similar message

### Step 4: Make a Test Fail (1 minute)

Let's see what a failing test looks like:

1. **Change** `assertEquals(1, 1);` to `assertEquals(1, 2);`
2. **Run the test again**
3. **Observe the failure message**:
   ```
   Expected: 1
   Actual: 2
   ```

**Important**: Change it back to `assertEquals(1, 1);` to make it pass again!

### Step 5: Complete Your First Challenge (3 minutes)

Find this test in the same file:

```java
@Test
@DisplayName("🎯 CHALLENGE: Complete this test yourself!")
void yourFirstChallenge() {
    // Challenge: Test the CARGO bike type
    // 1. Get a CARGO bike type
    // 2. Check that its display name is "Cargo Bike"
    // 3. Check that its rate is 1.20 (Swedish pricing)
    
    // TODO: Write your test code here!
```

**Your mission:**
1. **Uncomment** these lines (remove the `//`):
   ```java
   // BikeType cargoBike = BikeType.???;
   // assertEquals("???", cargoBike.getDisplayName());
   // assertEquals(???, cargoBike.getPricePerMinute());
   ```

2. **Replace the ???** with the correct values:
   ```java
   BikeType cargoBike = BikeType.CARGO;
   assertEquals("Cargo Bike", cargoBike.getDisplayName());
   assertEquals(1.20, cargoBike.getPricePerMinute());
   ```

3. **Comment out** this line (add `//` at the start):
   ```java
   // assertTrue(true);
   ```

4. **Run the test** to see if it passes!

### Step 6: Celebrate! 🎉

You just:
- ✅ Ran your first test
- ✅ Saw a test failure
- ✅ Fixed a test
- ✅ Wrote test code yourself

## What You Learned

**Testing Concepts:**
- What `@Test` annotation does
- How `assertEquals` works
- The difference between expected and actual values
- How to read test failure messages

**BikeShare Domain:**
- `BikeType.CARGO` represents cargo bikes
- Each bike type has a display name and price per minute
- Swedish pricing uses SEK currency

## What's Next?

**Easy Next Steps:**
1. **Try other tests** in `BeginnerFirstTest.java`
2. **Modify values** and see what happens
3. **Add your own simple test** using the same pattern

**When You're Ready:**
1. **Move to** `BikeBasicTest.java` for slightly harder challenges
2. **Try** the examples in `BEGINNERS_GUIDE.md`
3. **Read** the `TESTING_GLOSSARY.md` for more terms

## Quick Help

**If nothing happens when you run tests:**
- Check you have the `@Test` annotation
- Make sure your method is `public void`
- Try running all tests: `mvn test`

**If you get compile errors:**
- Check for typos in method names
- Make sure you have proper imports at the top
- Look for missing semicolons `;`

**If tests fail unexpectedly:**
- Read the error message carefully
- Check `Expected vs Actual` values
- Look at working examples for reference

## Remember

- **Everyone starts somewhere** - this is normal!
- **Failure is learning** - failed tests teach you things
- **Practice makes perfect** - try writing more tests
- **Ask questions** - testing is learned by doing

You've taken your first step into testing! 🚀

---

**Next file to try**: `BikeBasicTest.java` (slightly more challenging)  
**Need help?** Check `BEGINNERS_GUIDE.md` and `TESTING_GLOSSARY.md`
