#!/bin/bash

# Setup Verification Script for Testing Demo Project
# This script checks if all dependencies are installed correctly

echo "=========================================="
echo "Testing Demo - Setup Verification"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_command() {
    if command -v $1 &> /dev/null; then
        echo -e "${GREEN}✓${NC} $1 is installed: $($1 --version 2>&1 | head -n 1)"
        return 0
    else
        echo -e "${RED}✗${NC} $1 is NOT installed"
        return 1
    fi
}

echo "Checking Java dependencies..."
echo "------------------------------"
check_command java
check_command javac
check_command mvn
echo ""

echo "Checking Python dependencies..."
echo "--------------------------------"
check_command python3
check_command pip3
echo ""

echo "Checking JavaScript dependencies..."
echo "------------------------------------"
check_command node
check_command npm
echo ""

echo "=========================================="
echo "Running Tests in Each Language..."
echo "=========================================="
echo ""

# Java Tests
echo "1. Testing Java Project..."
echo "   Location: ./java"
if [ -d "./java" ]; then
    cd java
    if mvn test &> /dev/null; then
        echo -e "   ${GREEN}✓ Java tests PASSED${NC}"
    else
        echo -e "   ${RED}✗ Java tests FAILED${NC}"
        echo "   Run 'cd java && mvn test' to see details"
    fi
    cd ..
else
    echo -e "   ${YELLOW}⚠ Java directory not found${NC}"
fi
echo ""

# Python Tests
echo "2. Testing Python Project..."
echo "   Location: ./python"
if [ -d "./python" ]; then
    cd python
    # Check if dependencies are installed
    if pip3 list | grep pytest &> /dev/null; then
        if python3 -m pytest tests/ -v &> /dev/null; then
            echo -e "   ${GREEN}✓ Python tests PASSED${NC}"
        else
            echo -e "   ${RED}✗ Python tests FAILED${NC}"
            echo "   Run 'cd python && pytest tests/ -v' to see details"
        fi
    else
        echo -e "   ${YELLOW}⚠ pytest not installed. Run: pip3 install -r requirements.txt${NC}"
    fi
    cd ..
else
    echo -e "   ${YELLOW}⚠ Python directory not found${NC}"
fi
echo ""

# JavaScript Tests
echo "3. Testing JavaScript Project..."
echo "   Location: ./javascript"
if [ -d "./javascript" ]; then
    cd javascript
    # Check if dependencies are installed
    if [ -d "node_modules" ]; then
        if npm test &> /dev/null; then
            echo -e "   ${GREEN}✓ JavaScript tests PASSED${NC}"
        else
            echo -e "   ${RED}✗ JavaScript tests FAILED${NC}"
            echo "   Run 'cd javascript && npm test' to see details"
        fi
    else
        echo -e "   ${YELLOW}⚠ node_modules not found. Run: npm install${NC}"
    fi
    cd ..
else
    echo -e "   ${YELLOW}⚠ JavaScript directory not found${NC}"
fi
echo ""

echo "=========================================="
echo "Summary"
echo "=========================================="
echo ""
echo "If all tests passed, you're ready for the seminar!"
echo ""
echo "Quick commands:"
echo "  Java:       cd java && mvn test"
echo "  Python:     cd python && pytest tests/ -v"
echo "  JavaScript: cd javascript && npm test"
echo ""
echo "For detailed documentation, see:"
echo "  - README.md (main overview)"
echo "  - docs/SEMINAR_OUTLINE.md (presentation guide)"
echo "  - docs/QUICK_REFERENCE.md (command reference)"
echo ""
